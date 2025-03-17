package com.silverwing.dorothy.api.dao;

import com.silverwing.dorothy.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("SELECT u from Member u WHERE u.phone = :phone and u.status = 'ENABLED'")
    Optional<Member> findMemberByPhone(@Param("phone") String phone);

    Optional<Member> findMemberByUserId(int userId);

    @Query("SELECT u from Member u WHERE u.email = :email and u.status = 'ENABLED'")
    Optional<Member> findMemberByEmail(@Param("email") String email);
}
