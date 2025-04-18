package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.domain.Exception.CouponException;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.Reservation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {
    private final ReservationRepository reservationRepository;

    public List<Reservation> getStamps(int userId){
         return reservationRepository.getStamps( userId ).orElse(Collections.emptyList());
    }

    @Transactional
    public int convertCoupon(int userId, int convertNum) throws CouponException {
        List<Reservation> stamps = reservationRepository.getStamps( userId ).orElse(Collections.emptyList());
        if (stamps.size() < convertNum){
            throw new CouponException("can't convert stamp. you need at least "+convertNum);
        }

        return reservationRepository.convertStampToCoupon(convertNum);


    }
}
