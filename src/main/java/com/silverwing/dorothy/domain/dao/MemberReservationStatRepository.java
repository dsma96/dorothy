package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.api.dto.MemberStatDto;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.MemberReservationStat;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberReservationStatRepository extends JpaRepository<MemberReservationStat, Integer> {

    @Query("SELECT new com.silverwing.dorothy.api.dto.MemberStatDto( " +
            "u.userId, u.userName, u.memo, u.createDate, r.reservationCount, r.lastVisitDate , r.firstVisitDate) " +
            "FROM Member u " +
            "LEFT JOIN MemberReservationStat r ON u.userId = r.userId "
    )
    Page<MemberStatDto> getMemberStat(Pageable pageable);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value="TRUNCATE TABLE user_reservation_stat")
    void truncateTable();

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
        value="insert into user_reservation_stat (user_id,  reserve_cnt, last_visit_date, first_visit_date,update_dt) " +
               " select u.user_id, ifnull( r.cnt,0) as reserve_cnt, r.last as last_visit_date, r.first as first_visit_date, now() from user u "+
               " left outer join ( "+
               " select r.user_id , count(1) as cnt, max( r.start_date) as last, min( r.start_date) as first "+
               " from reservation r "+
               " where r.status = 'CREATED' " +
               " and r.user_id > 9 " +
               " group by r.user_id " +
               " ) r on u.user_id = r.user_id "
    )
    void refreshMemberStatTable();
}
