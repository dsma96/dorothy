package com.silverwing.dorothy.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silverwing.dorothy.api.dto.ReservationDto;
import com.silverwing.dorothy.api.dto.ReservationRequestDTO;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.HairServices;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import com.silverwing.dorothy.domain.type.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@Slf4j
public class ReserveController {

    private final ReservationService reservationService;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private final ReservationRepository reservationRepository;

    public ReserveController(ReservationService reservice,
                             ReservationRepository reservationRepository){
        this.reservationService = reservice;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservation/start")
    public ResponseEntity<ResponseData<List<ReservationDto>>> getStartDate(@AuthenticationPrincipal Member member, @RequestParam("startDate") String startDateStr, @RequestParam("endDate") String endDateStr) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            return new ResponseEntity<>(new ResponseData<>("Invalid date format"), HttpStatus.BAD_REQUEST);
        }

        List<Reservation> reservations = reservationService.getReservationsWithStartDate(startDate, endDate);
        List<ReservationDto> reservationDtos = reservationService.convertReservations(reservations, member.getUserId());
        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), reservationDtos), HttpStatus.OK);
    }


    @GetMapping("/reservations")
    public ResponseEntity< ResponseData<List<ReservationDto>>> getReservations(@AuthenticationPrincipal Member member, @RequestParam("startDate") String startDateStr, @RequestParam("endDate") String endDateStr) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        Date startDate = null;
        Date endDate = null;

        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            return new ResponseEntity<>(new ResponseData<>("Invalid date format"), HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();

        if( !member.isRootUser() ){
            if( startDate.before( now)){
                startDate = now;
            }
        }


        List<Reservation> reservations = reservationService.getReservations( startDate, endDate);
        List<ReservationDto> reservationDtos = reservationService.convertReservations(reservations, member.getUserId());
        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), reservationDtos), HttpStatus.OK);
    }

    @GetMapping("/{regId}")
    public ResponseEntity<ResponseData<ReservationDto>> getReservation(@AuthenticationPrincipal Member member, @PathVariable int regId) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        Reservation reservation = reservationService.getReservation(regId).orElseThrow();
        if( member.getUserId() != reservation.getUserId() && !member.isRootUser() ) {
            throw new ReserveException("You can't have the permission");
        }
        ReservationDto reservationDto = reservationService.convertReservation( reservation, member);
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), reservationDto), HttpStatus.OK);
    }

    @PostMapping("/reservation")
    public ResponseEntity<ResponseData<ReservationDto>> createReservation(@AuthenticationPrincipal Member member,
                                                                          @RequestParam(value="files", required = false) MultipartFile[] files,
                                                                          @RequestParam("reservation") String jsonData) {

        ReservationRequestDTO reservationDto;
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }
        try {
            reservationDto = new ObjectMapper().readValue(jsonData, ReservationRequestDTO.class);
        }catch(JsonProcessingException e){
            throw new ReserveException("Invalid JSON data");
        }

        Reservation reservation =  reservationService.createReservation(reservationDto, member, files);
        ReservationDto dto = reservationService.convertReservation( reservation, member);
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), dto), HttpStatus.OK);
    }

    @PutMapping("/reservation/{regId}/tip")
    public ResponseEntity<ResponseData<ReservationDto>> updateTip(@AuthenticationPrincipal Member member,
                                                                          @PathVariable int regId,
                                                                            @RequestParam("tip") float tip) {
        ReservationRequestDTO reservationDto;
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( !member.isRootUser()){
            throw new ReserveException("Not enough permission");
        }

        if( tip < 0 || tip > 100 ){
            throw new ReserveException("Invalid tip");
        }

        Reservation reservation =  reservationService.getReservation(regId).orElseThrow();

        reservation.setModifyDate( new Date());
        reservation.setModifier( member.getUserId());
        reservation.setTip( tip );
        reservationRepository.save(reservation);
        ReservationDto dto = reservationService.convertReservation( reservation, member);
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), dto), HttpStatus.OK);
    }

    @PutMapping("/reservation/{regId}")
    public ResponseEntity<ResponseData<ReservationDto>> updateReservation(@AuthenticationPrincipal Member member,
                                                                          @RequestParam(value = "files", required = false) MultipartFile[] files,
                                                                          @RequestParam("reservation") String jsonData,
                                                                          @PathVariable int regId) {
        ReservationRequestDTO reservationDto;
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }
        try {
            reservationDto = new ObjectMapper().readValue(jsonData, ReservationRequestDTO.class);
        }catch(JsonProcessingException e){
            throw new ReserveException("Invalid JSON data");
        }

        Reservation reservation =  reservationService.getReservation(regId).orElseThrow();

        Date now = new Date();

        if( reservation.getStartDate().before(now) && !member.isRootUser()){
            throw new ReserveException("You can't modify the registration in the past");
        }

        if( reservation.getUserId() != member.getUserId() && !member.isRootUser() ) {
            throw new ReserveException("You can't modify this registration");
        }

        reservation = reservationService.updateReservation( reservation, reservationDto, member,files);

        ReservationDto dto = reservationService.convertReservation( reservation, member);
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), dto), HttpStatus.OK);
    }


    @PutMapping("/cancel/{regId}")
    public ResponseEntity<ResponseData<ReservationDto>> cancelReservation(@AuthenticationPrincipal Member member, @PathVariable int regId) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        Reservation canceledReservation =  reservationService.cancelReservation(regId, member);
        ReservationDto reservationDto = reservationService.convertReservation( canceledReservation, member);
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), reservationDto), HttpStatus.OK);
    }

    @GetMapping("/services")
    public ResponseEntity<ResponseData<List<HairServices>>> getServices() {
        List<HairServices> services = reservationService.getHairServices();
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(), services), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseData<Page<ReservationDto>>> getHistory(
            @AuthenticationPrincipal Member member,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam int userId)
        {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("You must login first");
        }

        if( !member.isRootUser() && userId != member.getUserId()){
            throw new ReserveException("Not enough permission");
        }

        // Fetch paginated and sorted reservations
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        Page<Reservation> reservationsPage = reservationService.getHistory(userId, pageable);
        // Convert reservations to DTOs
        List<ReservationDto> reservationDtos = reservationService.convertReservations(reservationsPage.getContent(), member);
        Page<ReservationDto> reservationDtosPage = new PageImpl<>(reservationDtos, pageable, reservationsPage.getTotalElements());

        return new ResponseEntity<>(
                new ResponseData<>("OK", HttpStatus.OK.value(), reservationDtosPage),
                HttpStatus.OK
        );
    }

    @PutMapping("/extend/{regId}")
    public ResponseEntity<ResponseData<ReservationDto>> extendReservation(
            @AuthenticationPrincipal Member member, @PathVariable int regId) {
            if (member == null || !member.isRootUser()) {
                throw new AuthenticationCredentialsNotFoundException("Permission Error");
            }
        Reservation r = reservationService.adjustReservationPeriod(regId, 30, member.getUserId());
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(),  reservationService.convertReservation(r, member)), HttpStatus.OK);
    }

    @PutMapping("/shrink/{regId}")
    public ResponseEntity<ResponseData<ReservationDto>> shrinkReservation(
            @AuthenticationPrincipal Member member, @PathVariable int regId) {
        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Permission Error");
        }

        Reservation r = reservationService.adjustReservationPeriod(regId, -30, member.getUserId());
        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(),  reservationService.convertReservation(r, member)), HttpStatus.OK);
    }

    @PutMapping("/status/{regId}/{status}")
    public ResponseEntity<ResponseData<ReservationDto>> setReservationStatus( @AuthenticationPrincipal Member member, @PathVariable int regId, @PathVariable String status){
        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Permission Error");
        }

        ReservationStatus reservationStatus = ReservationStatus.valueOf(status);
        Reservation r = reservationService.setReservationStatus( regId, reservationStatus, member.getUserId());

        return new ResponseEntity<>( new ResponseData<>("OK", HttpStatus.OK.value(),  reservationService.convertReservation(r, member)), HttpStatus.OK);
    }
}


