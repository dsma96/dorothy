package com.silverwing.dorothy.domain.service.reserve;

import com.silverwing.dorothy.api.dto.HairSerivceDto;
import com.silverwing.dorothy.api.dto.UploadFileDto;
import com.silverwing.dorothy.domain.Exception.FileUploadException;
import com.silverwing.dorothy.domain.service.PhotoFileService;
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
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ReserveServiceMapRepository reserveServiceMapRepository;
    private final ServiceConfigRepository serviceConfigRepository;
    private final HairServiceRepository hairServiceRepository;
    private final OffDayRepository offDayRepository;
    private final NotificationService notificationService;
    private final PhotoFileService photoFileService;
    private final ObjectProvider<ReservationService> selfProvider;
    private final UploadFileRepository uploadFileRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private SimpleDateFormat dayOnly = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat hourOnly = new SimpleDateFormat("HH:mm");

    public List<Reservation> getReservations(Date startDate, Date endDate ) {
        List<Reservation> reservations;
            reservations = reservationRepository.findAllWithStartDateAndEndDate(startDate,endDate).orElseGet(()-> Collections.emptyList());
        return reservations;
    }

    public List<Reservation> getReservationWithStartDateAndServiceIds( Date startDate, Date endDate, List<Integer> serviceId ) {
        List<Reservation> reservations;
        reservations = reservationRepository.findStartDateAndService(startDate,endDate,serviceId).orElseGet(()-> Collections.emptyList());
        return reservations;
    }

    public List<Reservation> getReservationsWithStartDate( Date startDate, Date endDate ) {
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
        List<HairSerivceDto> hairServiceDtos = convertHairServices(hairServices, reservation.getStartDate());
        ReservationDto dto = ReservationDto.builder()
                .reservationId(reservation.getRegId())
                .userName( caller.isRootUser() || userId == reservation.getUserId()? reservation.getUser().getUserName() : "Occupied" )
                .phone( caller.isRootUser()|| userId == reservation.getUserId() ? reservation.getUser().getPhone() : "000-000-0000" )
                .startDate(sdf.format(reservation.getStartDate()))
                .endDate(sdf.format(reservation.getEndDate()))
                .createDate( reservation.getModifyDate().after( reservation.getStartDate()) ?  sdf.format(reservation.getModifyDate()) : sdf.format(reservation.getCreateDate()))
                .services(caller.isRootUser() || userId == reservation.getUserId() ? hairServiceDtos : Collections.emptyList())
                .status( reservation.getStatus())
                .isEditable( reservation.getStartDate().after(now) && (caller.isRootUser() || userId == reservation.getUserId()))
                .memo(caller.isRootUser() || userId == reservation.getUserId() ? reservation.getMemo() : "")
                .userId(reservation.getUserId())
                .isRequireSilence((caller.isRootUser() || userId == reservation.getUserId()) && reservation.isRequireSilence())
                .files( (caller.isRootUser() || userId == reservation.getUserId()) &&  reservation.getUploadFiles() != null ?  reservation.getUploadFiles().stream().map( r -> UploadFileDto.of(r)).toList() : Collections.emptyList() )
                .tip(caller.isRootUser()  ? reservation.getTip() : 0f)
                .build();
        return dto;
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, Member caller) {
        return reservations.stream().map( s-> convertReservation( s, caller )).toList();
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, int userId) {
        Member caller = memberRepository.findMemberByUserId( userId ).orElseThrow();
        return convertReservations( reservations, caller);
    }

    private Date calculateServiceTime(ReservationRequestDTO reqDto, Date startDate) {
        int totalTime = 0;
        List<HairServices> services = selfProvider.getObject().getHairServices(); // to use cache

        for( HairServices s : services ){
            if( reqDto.getServiceIds().contains(s.getServiceId())){
                totalTime += s.getServiceTime();
            }
        }

        Date endDate = new Date( startDate.getTime()+ totalTime * 60000); // in minutes
        return endDate;
    }

    static final int MANDATORY_ID= 1;

    private List<ReserveServiceMap> getHairServices(ReservationRequestDTO requestDTO,Reservation reservation) {
        ArrayList <ReserveServiceMap> hairServicesMap = new ArrayList<>();
        List <HairServices> hairServices = hairServiceRepository.findHairServicesByIds( requestDTO.getServiceIds()).orElseThrow();

        for( HairServices hs : hairServices ){
            ReserveServiceMap hairService = new ReserveServiceMap();
            hairService.setRegId( reservation.getRegId());
            hairService.setSvcId( hs.getServiceId());
            ServicePrice price = hs.getServicePrices().stream()
                    .filter( p -> reservation.getStartDate().after( p.getStartDate() ) && reservation.getStartDate().before( p.getEndDate()))
                    .findFirst()
                    .orElseThrow(()-> new ReserveException("Can't find service price: " + hs.getServiceId()+" at " + reservation.getStartDate()));
            hairService.setPrice(price.getPrice());
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

    public List<HairSerivceDto> convertHairServices(List<HairServices> services, Date regDate ) {
        if (services == null || services.isEmpty()) {
            return Collections.emptyList();
        }

        return services.stream().map( s -> {
            if( s == null )
                return null;

             return HairSerivceDto.builder()
                    .serviceId(s.getServiceId())
                    .name(s.getName())
                    .idx(s.getIdx())
                    .serviceTime(s.getServiceTime())
                     .price( s.getServicePrices().stream()
                             .filter( p -> regDate.after(p.getStartDate()) && regDate.before(p.getEndDate()))
                             .findFirst()
                             .orElseThrow(() -> new ReserveException("Can't find service price: " + s.getServiceId() + " at " + regDate))
                             .getPrice())
                    .build();

        }).toList();

    }

    @Transactional
    @Synchronized
    public Reservation updateReservation(Reservation reservation, ReservationRequestDTO reqDto, Member customer, MultipartFile[] files) {
        if (reqDto.getServiceIds() == null || reqDto.getServiceIds().isEmpty()) {
            throw new ReserveException("Reservation should have at least one service");
        }

        Date now = new Date();
        Date startDate = parseStartDate(reqDto.getStartTime());
        Date endDate = calculateServiceTime(reqDto, startDate);

        validateReservationTime(reqDto.getDesigner(), startDate, endDate, reservation.getRegId());
        handleFileUploads(reservation, reqDto, files);

        reservation.setMemo(reqDto.getMemo());
        reservation.setStatus(ReservationStatus.CREATED);
        reservation.setUserId(customer.getUserId());
        reservation.setModifyDate(now);
        reservation.setDesignerId(reqDto.getDesigner());
        reservation.setModifier(customer.getUserId());
        reservation.setRequireSilence(reqDto.isRequireSilence());
        reservation.setEndDate(endDate);

        Reservation persistedReservation = reservationRepository.save(reservation);
        updateServiceMappings(reqDto, persistedReservation);
//        notificationService.sendReservationChangedMessage(persistedReservation);
        return persistedReservation;
    }

    @Transactional
    @Synchronized
    public Reservation createReservation(ReservationRequestDTO reqDto, Member customer, MultipartFile[] files) {
        if (reqDto.getServiceIds() == null || reqDto.getServiceIds().isEmpty()) {
            throw new ReserveException("Reservation should have at least one service");
        }

        Date now = new Date();
        Date startDate = parseStartDate(reqDto.getStartTime());

        if (startDate.before(now)) {
            throw new ReserveException("Reservation should be in the future");
        }

        Date endDate = calculateServiceTime(reqDto, startDate);
        validateReservationTime(reqDto.getDesigner(), startDate, endDate, -1);

        Reservation reservation = new Reservation();
        reservation.setMemo(reqDto.getMemo());
        reservation.setStatus(ReservationStatus.CREATED);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setUserId(customer.getUserId());
        reservation.setCreateDate(now);
        reservation.setModifyDate(now);
        reservation.setDesignerId(reqDto.getDesigner());
        reservation.setModifier(customer.getUserId());
        reservation.setRequireSilence(reqDto.isRequireSilence());
        reservation.setUser(customer);

        reservation.setStampCount( customer.isRootUser()? 0 : 1);
        reservation.setCouponId(0);
        Reservation persistedReservation = reservationRepository.save(reservation);

        updateServiceMappings(reqDto, persistedReservation);
        handleFileUploads(persistedReservation, reqDto, files);

        notificationService.sendReservationMessage(persistedReservation);

        return persistedReservation;
    }

    private Date parseStartDate(String startTime) {
        try {
            return sdf.parse(startTime);
        } catch (ParseException e) {
            throw new ReserveException(e.getMessage());
        }
    }

    public  void validateReservationTime(int designerId, Date startDate, Date endDate, int excludeId) {

        Date now = new Date();

        String startDateDayOnly = dayOnly.format(startDate);
        try {
            OffDayId offId = new OffDayId(dayOnly.parse(startDateDayOnly), designerId);
            if (offDayRepository.findById(offId).isPresent()) {
                throw new ReserveException("Designer is having an off day");
            }
        } catch (ParseException e) {
            throw new ReserveException(e.getMessage());
        }

        List<Reservation> duplicatedReserve = reservationRepository.findAllWithDateOnDesigner(designerId, startDate, endDate, excludeId)
                .orElseGet(Collections::emptyList);

        if (!duplicatedReserve.isEmpty()) {
            throw new ReserveException("예약시간이 겹칩니다. \n Please select another time");
        }

        ServiceConfig config = serviceConfigRepository.findAll().stream().findFirst().orElseThrow();
        long dayBetween = (startDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);

        if (dayBetween >= config.getMaxReservationDate()) {
            throw new ReserveException("예약은 최대 " + config.getMaxReservationDate() + "일 전까지 가능합니다. \n You can only reserve up to " + config.getMaxReservationDate() + " days in advance.");
        }

        String endTime = hourOnly.format(endDate);
        if( endTime.compareTo(config.getCloseTime()) > 0){
            throw new ReserveException("영업 종료시간과 겹칩니다.\n Please select more earlier time");
        }
    }

    private void handleFileUploads(Reservation reservation, ReservationRequestDTO reqDto, MultipartFile[] files) {
        if (files != null && files.length > 0) {
            photoFileService.removeAllFilesByReserve(reservation.getRegId());
            try {
                List<UploadFile> uploadFiles = photoFileService.saveFiles(reservation, files);
                if (uploadFiles != null && !uploadFiles.isEmpty()) {
                    reservation.setUploadFiles(uploadFiles);
                }
            } catch (FileUploadException e) {
                log.error(e.getMessage());
            }
        } else if (reqDto.getFileIds() != null && !reqDto.getFileIds().isEmpty()) {  // partial remove
            Optional<List<UploadFile>> filesWrapper = uploadFileRepository.findByRegId(reservation.getRegId());
            if (filesWrapper.isPresent()) {
                List<UploadFile> uploadFiles = filesWrapper.get();
                for (UploadFile f : uploadFiles) {
                    if (!reqDto.getFileIds().contains(f.getFileId())) {
                        f.setFileStatus(FileUploadStatus.SHOULD_DELETE);
                        uploadFileRepository.save(f);
                    }
                }
            }
        } else  { // remove all
            List<UploadFile> uploadFiles = reservation.getUploadFiles();
            if (uploadFiles == null || uploadFiles.isEmpty()) {
                return;
            }
            for (UploadFile f : uploadFiles) {
                f.setFileStatus(FileUploadStatus.SHOULD_DELETE);
            }
            uploadFileRepository.saveAll(uploadFiles);
        }
    }


    private void updateServiceMappings(ReservationRequestDTO reqDto, Reservation reservation) {
        List<ReserveServiceMap> newServiceMap = getHairServices(reqDto, reservation);
        List<ReserveServiceMap> oldServiceMap = reservation.getServices();

        if (newServiceMap.isEmpty()) {
            throw new ReserveException("Can't create reservation without service");
        }
        if( oldServiceMap ==null ){
            oldServiceMap = Collections.emptyList();
        }

        boolean equal = newServiceMap.size() == oldServiceMap.size() && newServiceMap.containsAll(oldServiceMap);

        if (!equal) {
            reserveServiceMapRepository.deleteByRegId(reservation.getRegId());
            reserveServiceMapRepository.saveAll(newServiceMap);
            reservation.setServices(newServiceMap);
        }
    }

    public Page<Reservation> getHistory(int userId, Pageable pageable) {
        return reservationRepository.findByUserIdAndStatusCreated(userId, pageable);
    }

    public Reservation adjustReservationPeriod(int regId, int amount, int rootUserId) throws ReserveException{
        Reservation r = getReservation( regId).orElseThrow();
        int originPeriod = (int)(r.getEndDate().getTime() - r.getStartDate().getTime()) / 1000 /60;
        Date newEnd = new Date( r.getEndDate().getTime() + amount * 60 * 1000 );

        if( originPeriod + amount <= 0 ){
            throw new ReserveException("Can't reduce the period.");
        }

        validateReservationTime( r.getDesignerId(), r.getStartDate(), newEnd , regId);
        r.setEndDate( newEnd);
        r.setModifyDate(new Date());
        r.setModifier( rootUserId );
        return reservationRepository.save(r);
    }

    public Reservation setReservationStatus(int regId , ReservationStatus status , int rootUserId) throws ReserveException{
        Reservation r = getReservation(regId).orElseThrow();
        if( r.getStatus().equals( status))
            return r;

        if( r.getStatus().equals( ReservationStatus.CANCELED) )
            return r;
        if( r.getStatus().equals( ReservationStatus.NOSHOW))
            return r;

        log.info("change reg {} from {} to {}", regId, r.getStatus(), status);
        r.setStatus(status);
        r.setModifyDate(new Date());
        r.setModifier( rootUserId);
        return reservationRepository.save(r);
    }

    public Reservation getLastReservation(int userId, List<Integer> serviceIds) {
        return reservationRepository.findLastReservation(userId, serviceIds).orElse(null);
    }

}
