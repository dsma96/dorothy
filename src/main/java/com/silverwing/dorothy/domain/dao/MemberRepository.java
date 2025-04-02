package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.OffDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("SELECT u from Member u WHERE u.phone = :phone and u.status = 'ENABLED'")
    Optional<Member> findMemberByPhone(@Param("phone") String phone);

    Optional<Member> findMemberByUserId(int userId);

    @Query("SELECT u from Member u WHERE u.email = :email and u.status = 'ENABLED'")
    Optional<Member> findMemberByEmail(@Param("email") String email);


    @Query("SELECT u from Member u WHERE u.status = 'ENABLED' and u.role = 'DESIGNER' and u.userId not in (SELECT o.designer from com.silverwing.dorothy.domain.entity.OffDay o where o.offDay = :bizDate)")
    Optional<List<Member>>  findAvailableDesigners(Date bizDate);
}
