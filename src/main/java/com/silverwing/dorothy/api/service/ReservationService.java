package com.silverwing.dorothy.api.service;

import com.silverwing.dorothy.api.dao.HairServiceRepository;
import com.silverwing.dorothy.api.dao.MemberRepository;
import com.silverwing.dorothy.api.dao.ReservationRepository;
import com.silverwing.dorothy.api.dao.ReserveServiceMapRepository;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.reserve.*;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReserveServiceMapRepository serviceMapRepository;
    private final ReserveServiceMapRepository reserveServiceMapRepository;
    private final HairServiceRepository hairServiceRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");

    public ReservationService(ReservationRepository ReservationRepository, MemberRepository MemberRepository, ReserveServiceMapRepository serviceMapRepository, ReserveServiceMapRepository reserveServiceMapRepository, HairServiceRepository hairServiceRepository) {
        this.reservationRepository = ReservationRepository;
        this.memberRepository = MemberRepository;
        this.serviceMapRepository = serviceMapRepository;
        this.reserveServiceMapRepository = reserveServiceMapRepository;
        this.hairServiceRepository = hairServiceRepository;
    }

    public List<Reservation> getReservations(int userId, Date startDate, Date endDate ) {
        List<Reservation> reservations;
            reservations = reservationRepository.findAllWithStartDate(startDate,endDate).orElseGet(()-> Collections.emptyList());
        return reservations;
    }

    public Optional<Reservation> getReservation(int reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public ReservationDto convertReservation(Reservation reservation, int userId){
        return convertReservation( reservation, memberRepository.findMemberByUserId(userId).orElseThrow());
    }

    public ReservationDto convertReservation(Reservation reservation, Member caller){
        int userId = caller.getUserId();
        Date now = new Date();
        List<HairServices> hairServices = reservation.getServices().stream().map( s-> s.getService()).toList();
        ReservationDto dto = ReservationDto.builder()
                .reservationId(reservation.getRegId())
                .userName( caller.isRootUser() || userId == reservation.getUserId()? reservation.getUser().getUsername() : "Occupied" )
                .phone( caller.isRootUser()|| userId == reservation.getUserId() ? reservation.getUser().getPhone() : "416-000-1234" )
                .startDate(sdf.format(reservation.getStartDate()))
                .createDate( reservation.getModifyDate().after( reservation.getStartDate()) ?  sdf.format(reservation.getModifyDate()) : sdf.format(reservation.getCreateDate()))
                .services(caller.isRootUser() || userId == reservation.getUserId() ? hairServices : Collections.emptyList())
                .status( caller.isRootUser() || userId == reservation.getUserId() ? reservation.getStatus() : ReservationStatus.CREATED)
                .isEditable( reservation.getStartDate().after(now) && (caller.isRootUser() || userId == reservation.getUserId()))
                .memo(caller.isRootUser() || userId == reservation.getUserId() ? reservation.getMemo() : "")
                .isRequireSilence((caller.isRootUser() || userId == reservation.getUserId()) && reservation.isRequireSilence())
                .build();
        return dto;
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, int userId) {
        Member caller = memberRepository.findMemberByUserId( userId ).orElseThrow();
        return reservations.stream().map( s-> convertReservation( s, caller )).toList();
    }

    @Transactional
    public Reservation updateReservation( Reservation reservation, ReservationRequestDTO reqDto, Member customer){
        Date now = new Date();

        reservation.setMemo( reqDto.getMemo());
        reservation.setStatus(ReservationStatus.CREATED);
        reservation.setUserId( customer.getUserId());
        reservation.setModifyDate(now);
        reservation.setDesignerId(reqDto.getDesigner());
        reservation.setModifier( customer.getUserId() );
        reservation.setRequireSilence( reqDto.isRequireSilence());
        Reservation persistedReservation = reservationRepository.save(reservation);
        List<ReserveServiceMap> newServiceMap = getHairServices(reqDto, persistedReservation.getRegId());
        List<ReserveServiceMap> oldServiceMap = reservation.getServices();


        if(newServiceMap.isEmpty()){
            throw new ReserveException("Can't create reservation without service");
        }

        boolean equal = newServiceMap.size() == oldServiceMap.size();

        if(!equal || !newServiceMap.containsAll(oldServiceMap)){
            reserveServiceMapRepository.deleteByRegId(reservation.getRegId());
            reserveServiceMapRepository.saveAll(newServiceMap);
            persistedReservation.setServices( newServiceMap );
        }

        return persistedReservation;
    }

    @Transactional
    public Reservation createReservation( ReservationRequestDTO reqDto, Member customer ) throws ReserveException {
        Date now = new Date();

        Date startDate;

        try{
            startDate = sdf.parse( reqDto.getStartTime());
        } catch(ParseException e){
            throw new ReserveException(e.getMessage());
        }

        Date endDate = new Date( startDate.getTime()+ 1800000);

        if( startDate.before( now) ){
            throw new ReserveException(" reservation should be in the future");
        }

        List<Reservation> duplicatedReserve = reservationRepository.findAllWithDateOnDesigner( reqDto.getDesigner(),startDate,endDate ).orElseGet(()-> Collections.emptyList());

        if(!duplicatedReserve.isEmpty()){
            throw new ReserveException("Duplicated time with Reservation: "+duplicatedReserve.get(0).getRegId()+" "+reqDto.getStartTime());
        }

        Reservation reservation = new Reservation();

        reservation.setMemo( reqDto.getMemo());
        reservation.setStatus(ReservationStatus.CREATED);
        reservation.setStartDate( startDate );
        reservation.setEndDate( endDate );
        reservation.setUserId( customer.getUserId());
        reservation.setCreateDate(now);
        reservation.setModifyDate(now);
        reservation.setDesignerId(reqDto.getDesigner());
        reservation.setModifier( customer.getUserId() );
        reservation.setRequireSilence( reqDto.isRequireSilence());

        Reservation persistedReservation =  reservationRepository.save( reservation );
        List<ReserveServiceMap> map = getHairServices(reqDto, persistedReservation.getRegId());

        if(map.isEmpty()){
            throw new ReserveException("Can't create reservation without service");
        }

        reserveServiceMapRepository.saveAll(map);
        persistedReservation.setServices( map );
        persistedReservation.setUser( customer);
        return persistedReservation;
    }




    static final int MANDATORY_ID= 1;

    private List<ReserveServiceMap> getHairServices(ReservationRequestDTO reservation, int regId) {
        ArrayList <ReserveServiceMap> hairServicesMap = new ArrayList<>();
        List <HairServices> hairServices = hairServiceRepository.findHairServicesByIds( reservation.getServiceIds()).orElseThrow();

        for( HairServices hs : hairServices ){
            ReserveServiceMap hairService = new ReserveServiceMap();
            hairService.setRegId(regId);
            hairService.setSvcId( hs.getServiceId());
            hairServicesMap.add( hairService );
        }
        return hairServicesMap;
    }

    public Reservation cancelReservation( int regId , Member caller){
        Reservation r = reservationRepository.findById(regId).orElseThrow();
        Date now = new Date();
        if( r.getStartDate().before( now )){
            throw new ReserveException(" You cannot cancel a reservation that has already passed");
        }

        if( r.getUserId() != caller.getUserId() && !caller.isRootUser() ){
            throw new ReserveException(caller.getUserId() +" can't cancel the reservation: "+r.getRegId());
        }

        if( r.getStatus() == ReservationStatus.CREATED) {
            r.setStatus(ReservationStatus.CANCELED);
            r.setModifyDate(now);
            r.setModifier(caller.getUserId());
        }
        else
            throw new ReserveException("can't cancel reservation");

        return reservationRepository.save( r );
    }

    @CacheEvict(value = "myCache")
    public List<HairServices> getHairServices() {
        return hairServiceRepository.getAvailableServices().orElseThrow();
    }
}
