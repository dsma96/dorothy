package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerifyRequestRepository extends JpaRepository<VerifyRequest, Integer> {
    @Query(nativeQuery = true,value= "SELECT * from verify_request v WHERE v.phone_no = :phoneNo and v.verify_type = :verifyType and v.verify_state = 'CREATED' and v.expire_date > now() LIMIT 1 ")
    Optional<VerifyRequest> findLiveVerify(@Param("phoneNo") String phoneNumber, @Param("verifyType")VerifyType verifyType);

    @Query(nativeQuery = true, value= "SELECT COUNT(1) from verify_request v where  v.phone_no = :phoneNo and v.verify_type = :verifyType and v.create_date > DATE_SUB( NOW(), INTERVAL 24 HOUR)")
    int countDailyTry(String phoneNo, VerifyType verifyType);

    @Query(nativeQuery = true,value= "SELECT * from verify_request v WHERE v.phone_no = :phoneNo and v.verify_type = :verifyType and v.verify_state = :verifyState and v.expire_date > now() LIMIT 1 ")
    Optional<VerifyRequest> findVerify(@Param("phoneNo") String phoneNumber, @Param("verifyType")VerifyType verifyType, @Param("verifyState") VerifyState verifyState);

}