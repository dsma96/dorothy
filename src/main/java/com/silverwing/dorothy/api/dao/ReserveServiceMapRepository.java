package com.silverwing.dorothy.api.dao;
import com.silverwing.dorothy.domain.reserve.HairServices;
import com.silverwing.dorothy.domain.reserve.Reservation;
import com.silverwing.dorothy.domain.reserve.ReserveServiceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReserveServiceMapRepository extends JpaRepository<ReserveServiceMap, Integer> {
    @Modifying
    @Query( value=" DELETE FROM ReserveServiceMap M where M.regId = :regId")
    int deleteByRegId(@Param("regId") int regId);
}
