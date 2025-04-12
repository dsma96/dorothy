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
                .phone( caller.isRootUser()|| userId == reservation.getUserId() ? reservation.getUser().getPhone() : "000-000-0000" )
                .startDate(sdf.format(reservation.getStartDate()))
                .endDate(sdf.format(reservation.getEndDate()))
                .createDate( reservation.getModifyDate().after( reservation.getStartDate()) ?  sdf.format(reservation.getModifyDate()) : sdf.format(reservation.getCreateDate()))
                .services(caller.isRootUser() || userId == reservation.getUserId() ? hairServices : Collections.emptyList())
                .status( reservation.getStatus())
                .isEditable( reservation.getStartDate().after(now) && (caller.isRootUser() || userId == reservation.getUserId()))
                .memo(caller.isRootUser() || userId == reservation.getUserId() ? reservation.getMemo() : "")
                .isRequireSilence((caller.isRootUser() || userId == reservation.getUserId()) && reservation.isRequireSilence())
                .files( (caller.isRootUser() || userId == reservation.getUserId()) &&  reservation.getUploadFiles() != null ?  reservation.getUploadFiles().stream().map( r -> UploadFileDto.of(r)).toList() : Collections.emptyList() )
                .build();
        return dto;
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, int userId) {
        Member caller = memberRepository.findMemberByUserId( userId ).orElseThrow();
        return reservations.stream().map( s-> convertReservation( s, caller )).toList();
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

    @Transactional
    public Reservation updateReservation(Reservation reservation, ReservationRequestDTO reqDto, Member customer, MultipartFile[] files) {
        if (reqDto.getServiceIds() == null || reqDto.getServiceIds().isEmpty()) {
            throw new ReserveException("Reservation should have at least one service");
        }

        Date now = new Date();
        Date startDate = parseStartDate(reqDto.getStartTime());
        Date endDate = calculateServiceTime(reqDto, startDate);

        validateReservationTime(reqDto, startDate, endDate, reservation.getRegId());
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
        notificationService.sendReservationChangedMessage(persistedReservation);
        return persistedReservation;
    }

    @Transactional
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
        validateReservationTime(reqDto, startDate, endDate, -1);

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

    private void validateReservationTime(ReservationRequestDTO reqDto, Date startDate, Date endDate, int excludeId) {
        String startDateDayOnly = dayOnly.format(startDate);
        try {
            OffDayId offId = new OffDayId(dayOnly.parse(startDateDayOnly), reqDto.getDesigner());
            if (offDayRepository.findById(offId).isPresent()) {
                throw new ReserveException("Designer is having an off day");
            }
        } catch (ParseException e) {
            throw new ReserveException(e.getMessage());
        }

        List<Reservation> duplicatedReserve = reservationRepository.findAllWithDateOnDesigner(reqDto.getDesigner(), startDate, endDate, excludeId)
                .orElseGet(Collections::emptyList);

        if (!duplicatedReserve.isEmpty()) {
            throw new ReserveException("예약시간이 겹칩니다. \n You should select another time");
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
        List<ReserveServiceMap> newServiceMap = getHairServices(reqDto, reservation.getRegId());
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
}
