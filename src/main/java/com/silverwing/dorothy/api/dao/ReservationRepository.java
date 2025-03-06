package com.silverwing.dorothy.api.dao;

import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.reserve.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<List<Reservation>> findAllByUserId(int userId);
    @Query("SELECT r From Reservation r WHERE r.userId = :userId and r.startDate between :startDate and :endDate" )
    Optional<List<Reservation>> findAllWithStartDate(@Param("userId")int userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    @Query("SELECT r From Reservation r WHERE r.startDate between :startDate and :endDate" )
    Optional<List<Reservation>> findAllWithStartDate( @Param("startDate")Date startDate, @Param("endDate")Date endDate );


}
