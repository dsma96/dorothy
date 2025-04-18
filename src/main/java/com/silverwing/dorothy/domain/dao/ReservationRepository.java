package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Optional<List<Reservation>> findAllByUserId(int userId);
    @Query("SELECT r From Reservation r WHERE r.userId = :userId and r.startDate between :startDate and :endDate and r.status != 'CANCELED'" )
    Optional<List<Reservation>> findAllWithStartDate(@Param("userId")int userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    @Query("SELECT r From Reservation r WHERE r.startDate between :startDate and :endDate and r.status != 'CANCELED'" )
    Optional<List<Reservation>> findAllWithStartDate( @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    @Query("SELECT r from Reservation r WHERE r.designerId = :designerId and r.startDate < :endDate and  r.endDate  > :startDate and r.status != 'CANCELED' and r.regId != :exclude")
    Optional<List<Reservation>> findAllWithDateOnDesigner(@Param("designerId") int designerId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("exclude") int excludeId );

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.status IN ('CREATED', 'COMPLETED') ")
    Page<Reservation> findByUserIdAndStatusCreated(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.status IN ('CREATED', 'COMPLETED') and r.stampCount > 0 and r.couponConverted = false order by r.startDate DESC")
    Optional<List<Reservation>> getStamps(@Param("userId") int userId);

    @Modifying
    @Query("UPDATE Reservation r SET r.couponConverted = true WHERE r.stampCount > 0 AND r.couponConverted = false and r.userId = :userId AND r.status IN ('CREATED', 'COMPLETED')")
    int convertStampToCoupon(@Param("userId")int userId);

}
