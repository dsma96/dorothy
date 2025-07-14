package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.api.dto.SaleStatDto;
import com.silverwing.dorothy.api.dto.StampDto;
import com.silverwing.dorothy.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r join fetch r.user u join fetch r.services join fetch r.uploadFiles WHERE r.regId = :regId")
    Optional<Reservation> findBy(int regId);


    Optional<List<Reservation>> findAllByUserId(int userId);
    @Query("SELECT r From Reservation r WHERE r.userId = :userId and r.startDate between :startDate and :endDate and r.status != 'CANCELED'" )
    Optional<List<Reservation>> findAllWithStartDate(@Param("userId")int userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate );


    @Query("SELECT r From Reservation r WHERE r.startDate < :endDate and r.endDate > :startDate and r.status != 'CANCELED'" )
    Optional<List<Reservation>> findAllWithStartDateAndEndDate( @Param("startDate")Date startDate, @Param("endDate")Date endDate );


    @Query("SELECT r From Reservation r WHERE r.startDate >= :startDate and r.startDate < :endDate and r.status = 'CREATED'" )
    Optional<List<Reservation>> findAllWithStartDate( @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    @Query(nativeQuery = true,
            value="SELECT r.* "+
                    "FROM reservation r , reserve_services rs " +
                    "WHERE r.reg_id = rs.reg_id  and r.start_date between :startDate and :endDate and r.status = 'CREATED' and rs.svc_id in (:serviceIds)" )
    Optional<List<Reservation>> findStartDateAndService(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("serviceIds") List<Integer> serviceIds );


    @Query(nativeQuery = true, value="""
        SELECT r.*
        FROM reservation r , reserve_services rs
        WHERE r.reg_id = rs.reg_id
          and r.user_id = :userId
            and r.status = 'CREATED' and rs.svc_id in (:serviceIds)
        order by r.start_date desc
        limit 1
        """)
    Optional<Reservation> findLastReservation(@Param("userId") int userId, @Param("serviceIds") List<Integer> serviceIds  );


    @Query("SELECT r from Reservation r WHERE r.designerId = :designerId and r.startDate < :endDate and  r.endDate  > :startDate and r.status != 'CANCELED' and r.regId != :exclude")
    Optional<List<Reservation>> findAllWithDateOnDesigner(@Param("designerId") int designerId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("exclude") int excludeId );

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.status IN ('CREATED', 'COMPLETED') ")
    Page<Reservation> findByUserIdAndStatusCreated(@Param("userId") int userId, Pageable pageable);

//    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.startDate < :until AND r.status IN ('CREATED', 'COMPLETED') and r.stampCount > 0 and r.couponConverted = false order by r.startDate")
    @Query(nativeQuery = true,
            value="select r.user_id as userId, DATE_FORMAT(r.start_date,'%y/%m/%d') as serviceDate, max(r.stamp_count) as stampCount from reservation r  "+
                    "where r.user_id = :userId and r.start_date < :until AND r.status IN ('CREATED', 'COMPLETED') and r.stamp_count > 0 and r.coupon_id = 0 "+
                    "GROUP BY r.user_id, DATE_FORMAT(r.start_date, '%y/%m/%d') " +
                    "ORDER BY r.start_date "+
                    "LIMIT :maxRows"
    )

    Optional<List<StampDto>> getStamps(@Param("userId") int userId, @Param("until") Date until, @Param("maxRows") int maxRows);

    @Modifying
    @Query(nativeQuery = true,
            value= "UPDATE reservation r SET r.coupon_id = :couponId "+
                    "WHERE r.stamp_count > 0 AND r.coupon_id = 0 "+
                    " and r.user_id = :userId and  date_format( start_date,'%y/%m/%d') in ( :dates ) AND r.status IN ('CREATED', 'COMPLETED')")
    int convertStampToCoupon(@Param("userId")int userId, @Param("dates") List<String> dates, @Param("couponId") int couponId);

    @Query(value = """
        SELECT 
            TO_CHAR(R.start_date, 'yy/mm') AS period,
            COUNT(*) AS totalCount,
            SUM(RS.price + R.TIP) AS totalSale,
            COUNT(CASE WHEN S.svc_id = 1 THEN 1 ELSE NULL END) AS manCutCount,
            SUM(CASE WHEN S.svc_id = 1 THEN RS.price + R.TIP ELSE 0 END) AS manCutSale,
            COUNT(CASE WHEN S.svc_id = 2 THEN 1 ELSE NULL END) AS manRootCount,
            SUM(CASE WHEN S.svc_id = 2 THEN RS.price + R.TIP  ELSE 0 END) AS manRootSale,
            COUNT(CASE WHEN S.svc_id IN (3, 4) THEN 1 ELSE NULL END) AS manPermCount,
            SUM(CASE WHEN S.svc_id IN (3, 4) THEN RS.price  + R.TIP ELSE 0 END) AS manPermSale,
            COUNT(CASE WHEN S.svc_id = 7 THEN 1 ELSE NULL END) AS womanCutCount,
            SUM(CASE WHEN S.svc_id = 7 THEN RS.price  + R.TIP ELSE 0 END) AS womanCutSale,
            COUNT(CASE WHEN S.svc_id = 8 THEN 1 ELSE NULL END) AS womanRootCount,
            SUM(CASE WHEN S.svc_id = 8 THEN RS.price  + R.TIP ELSE 0 END) AS womanRootSale
        FROM 
            reservation R
        JOIN 
            reserve_services RS ON R.reg_id = RS.reg_id
        JOIN 
            services S ON RS.svc_id = S.svc_id
        WHERE 
            R.status = 'CREATED'
            AND R.user_id > 9
            AND R.stamp_count > 0
            AND R.start_date <= now()
            AND to_char(R.start_date,'yyyy') = :year
        GROUP BY 
            TO_CHAR(R.start_date, 'yy/mm')
        """, nativeQuery = true)
    List<SaleStatDto> getMonthlySaleStat(String year);

}
