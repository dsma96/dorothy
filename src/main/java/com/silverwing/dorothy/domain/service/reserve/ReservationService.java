package com.silverwing.dorothy.domain.service.reserve;

import com.silverwing.dorothy.api.dto.UploadFileDto;
import com.silverwing.dorothy.domain.Exception.FileUploadException;
import com.silverwing.dorothy.domain.service.file.PhotoFileService;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.dao.*;
import com.silverwing.dorothy.api.dto.ReservationDto;
import com.silverwing.dorothy.api.dto.ReservationRequestDTO;
import com.silverwing.dorothy.domain.entity.*;

import com.silverwing.dorothy.domain.type.FileUploadStatus;
import com.silverwing.dorothy.domain.type.ReservationStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@CacheConfig(cacheNames="reservation")
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReserveServiceMapRepository serviceMapRepository;
    private final ReserveServiceMapRepository reserveServiceMapRepository;
    private final HairServiceRepository hairServiceRepository;
    private final OffDayRepository offDayRepository;
    private final NotificationService notificationService;
    private final PhotoFileService photoFileService;
    private final ObjectProvider<ReservationService> selfProvider;
    private final UploadFileRepository uploadFileRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private SimpleDateFormat dayOnly = new SimpleDateFormat("yyyyMMdd");

    public List<Reservation> getReservations(Date startDate, Date endDate ) {
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
        List<HairServices> hairServices = reservation.getServices().stream().map(s-> s.getService()).toList();
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
                .files( reservation.getUploadFiles().isEmpty() ? Collections.emptyList() :  reservation.getUploadFiles().stream().map( r -> UploadFileDto.of(r)).toList() )
                .build();
        return dto;
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, int userId) {
        Member caller = memberRepository.findMemberByUserId( userId ).orElseThrow();
        return reservations.stream().map( s-> convertReservation( s, caller )).toList();
    }

    @Transactional
    public Reservation updateReservation(Reservation reservation, ReservationRequestDTO reqDto, Member customer,MultipartFile[] files ){
        Date now = new Date();

        if( files != null && files.length > 0){
            photoFileService.removeAllFilesByReserve(reservation.getRegId());
            List<UploadFile> uploadFiles = null;

            try {
                uploadFiles =  photoFileService.saveFiles(reservation, files);
            }catch (FileUploadException e){
                log.error( e.getMessage() ); // just ignore
            }

            if( uploadFiles != null && !uploadFiles.isEmpty()) {
                reservation.setUploadFiles(uploadFiles);
            }
        }
        else if( reqDto.getFileIds() != null && !reqDto.getFileIds().isEmpty()){ // partial remove
            Optional<List<UploadFile>> filesWrapper =  uploadFileRepository.findByRegId(reservation.getRegId() );
            if( filesWrapper.isPresent() ){
                List<UploadFile> uploadFiles = filesWrapper.get();
                for( UploadFile f : uploadFiles ){
                    if( !reqDto.getFileIds().contains(f.getFileId())){
                        f.setFileStatus(FileUploadStatus.SHOULD_DELETE);
                        uploadFileRepository.save(f);
                    }
                }
            }
        }else if(reservation.getUploadFiles() != null && !reservation.getUploadFiles().isEmpty()){ // remove all
            List<UploadFile> uploadFiles = reservation.getUploadFiles();
            for( UploadFile f : uploadFiles ){
                f.setFileStatus(FileUploadStatus.SHOULD_DELETE);
            }
            uploadFileRepository.saveAll(uploadFiles);
        }

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


    public Reservation createReservation( ReservationRequestDTO reqDto, Member customer, MultipartFile[] files ) throws ReserveException {
       Reservation newReservation = selfProvider.getObject().createReservation( reqDto, customer); // for transaction

       List<UploadFile> uploadFiles = null;
       try {
           uploadFiles =  photoFileService.saveFiles(newReservation, files);
       }catch (FileUploadException e){
           log.error( e.getMessage() ); // just ignore
       }

         if( uploadFiles != null && !uploadFiles.isEmpty()) {
             newReservation.setUploadFiles(uploadFiles);
         }
        return newReservation;
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

        String startDateDayOnly = dayOnly.format(startDate);
        try {
            OffDayId offId = new OffDayId(dayOnly.parse(startDateDayOnly), reqDto.getDesigner());

            if (offDayRepository.findById(offId).isPresent()) {
                throw new ReserveException("Designer is having an off day");
            }
        }catch(ParseException e){
            throw new ReserveException(e.getMessage());
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

        notificationService.sendReservationMessage(persistedReservation);

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
        notificationService.sendReservationCancelMessage(r);
        return reservationRepository.save( r );
    }


    @Cacheable(cacheNames = "hairservice")
    public List<HairServices> getHairServices() {
        return hairServiceRepository.getAvailableServices().orElseThrow();
    }
}
