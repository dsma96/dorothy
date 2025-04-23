package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.api.dto.StampDto;
import com.silverwing.dorothy.domain.Exception.CouponException;
import com.silverwing.dorothy.domain.dao.CouponRepository;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.Coupon;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.type.CouponStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    @Value("${dorothy.coupon.maxstamp:10}")
    private int maxStampCount ;

    private final ReservationRepository reservationRepository;
    private final CouponRepository couponRepository;

    public List<StampDto> getStamps(int userId){
        Date now = new Date();
        Date until = new Date( now.getYear(), now.getMonth(), now.getDate());
         return reservationRepository.getStamps( userId, until , maxStampCount).orElse(Collections.emptyList());
    }

    @Transactional
    public int convertCoupon(int userId, int regId, Member caller) throws CouponException {
        Date now = new Date();
        Date until = new Date( now.getYear(), now.getMonth(), now.getDate());
        Reservation r = reservationRepository.findById( regId ).orElseThrow();
        r.setStampCount(0);
        reservationRepository.save(r);

        if( r.getUserId() != userId ){
            log.error("can't apply coupon reservationUser:{} requestId{}", r.getUserId(), userId);
            throw new CouponException("Can't apply coupon");
        }

        List<StampDto> stamps = reservationRepository.getStamps( userId, until, maxStampCount ).orElse(Collections.emptyList());
        if (stamps.size() < maxStampCount){
            throw new CouponException("can't convert stamp. you need more than "+maxStampCount);
        }

        Coupon c = new Coupon();
        c.setUserId(userId);
        c.setCreateDate(now);
        c.setModifyDate(now);
        c.setCreateId( caller.getUserId());
        c.setStampCount(maxStampCount);
        c.setUsedRegId(regId);
        c.setStatus(CouponStatus.USED);
        Coupon createdCoupon = couponRepository.save(c);

        List<String> dates = stamps.stream().map( s-> s.getServiceDate()).toList();

        reservationRepository.convertStampToCoupon(userId, dates,createdCoupon.getId());

        return getStamps(userId).size();


    }

}
