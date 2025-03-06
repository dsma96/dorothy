package com.silverwing.dorothy.api.service;

import com.silverwing.dorothy.api.dao.MemberRepository;
import com.silverwing.dorothy.api.dao.ReservationRepository;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.reserve.HairServices;
import com.silverwing.dorothy.domain.reserve.Reservation;
import com.silverwing.dorothy.domain.reserve.ReservationDto;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository ReservationRepository, MemberRepository MemberRepository) {
        this.reservationRepository = ReservationRepository;
        this.memberRepository = MemberRepository;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");

    public List<Reservation> getReservations(int userId, Date startDate, Date endDate ) {
        List<Reservation> reservations;

        if( userId > 0){
            reservations = reservationRepository.findAllWithStartDate(userId, startDate,endDate).orElseGet(()-> Collections.emptyList());
        }else{
            reservations = reservationRepository.findAllWithStartDate(startDate,endDate).orElseGet(()-> Collections.emptyList());
        }
        return reservations;
    }

    public List<ReservationDto> convertReservations(List<Reservation> reservations, int userId) {
        Member caller = memberRepository.findMemberByUserId( userId ).orElseThrow();

        List<ReservationDto> reservationDtos = new ArrayList<>();

        for( Reservation reservation : reservations ){
            List<HairServices> hairServices = reservation.getServices().stream().map( s-> s.getService()).toList();
            ReservationDto dto = ReservationDto.builder()
                    .reservationId(reservation.getRegId())
                    .userName( caller.isRootUser() || userId == caller.getUserId()? reservation.getUser().getUsername() : "John Doe" )
                    .phone( caller.isRootUser()|| userId == caller.getUserId() ? reservation.getUser().getPhone() : "416-000-1234" )
                    .startDate(sdf.format(reservation.getStartDate()) )
                    .services(caller.isRootUser() || userId == caller.getUserId() ? hairServices : Collections.emptyList())
                    .build();
            reservationDtos.add(dto);
        }
        return reservationDtos;
    }
}
