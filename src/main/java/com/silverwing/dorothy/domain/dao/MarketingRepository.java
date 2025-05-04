package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.Marketing;
import com.silverwing.dorothy.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MarketingRepository extends JpaRepository<Marketing, Integer>{

    @Query("SELECT m FROM Marketing m WHERE m.startDate <= CURRENT_DATE and m.endDate >= CURRENT_DATE and m.used = true")
    Optional<List<Marketing>> getAvailableMarketings();
}
