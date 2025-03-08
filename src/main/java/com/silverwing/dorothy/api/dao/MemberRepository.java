package com.silverwing.dorothy.api.dao;

import com.silverwing.dorothy.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByPhone(String phone);
    Optional<Member> findMemberByUserId(int userId);

}
