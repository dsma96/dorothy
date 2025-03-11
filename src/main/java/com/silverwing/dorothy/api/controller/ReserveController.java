package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dao.ReservationRepository;
import com.silverwing.dorothy.api.service.ReservationService;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.member.MemberDto;
import com.silverwing.dorothy.domain.reserve.Reservation;
import com.silverwing.dorothy.domain.reserve.ReservationDto;
import com.silverwing.dorothy.domain.reserve.ReservationRequestDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
public class ReserveController {

    private final ReservationService reservationService;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private final ReservationRepository reservationRepository;

    public ReserveController(ReservationService reservice,
                             ReservationRepository reservationRepository){
        this.reservationService = reservice;
        this.reservationRepository = reservationRepository;
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


        List<Reservation> reservations = reservationService.getReservations(member.getUserId(), startDate, endDate);
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
    public ResponseEntity<ResponseData<ReservationDto>> createReservation(@AuthenticationPrincipal Member member, @RequestBody ReservationRequestDTO reservationDto) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        Reservation reservation =  reservationService.createReservation(reservationDto, member);
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

}


