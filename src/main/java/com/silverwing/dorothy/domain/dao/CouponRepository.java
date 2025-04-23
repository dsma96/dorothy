package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository  extends JpaRepository<Coupon, Integer> {
}
