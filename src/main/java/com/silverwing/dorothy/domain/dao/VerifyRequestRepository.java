package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerifyRequestRepository extends JpaRepository<VerifyRequest, Integer> {
    @Query(nativeQuery = true,value= "SELECT * from verify_request v WHERE v.phone_no = :phoneNo and v.verify_type = :verifyType and v.verify_state = 'CREATED' and v.expire_date > now() LIMIT 1 ")
    Optional<VerifyRequest> findLiveVerify(@Param("phoneNo") String phoneNumber, @Param("verifyType")String verifyType);

    @Query(nativeQuery = true, value= "SELECT COUNT(1) from verify_request v where  v.phone_no = :phoneNo and v.verify_type = :verifyType and v.create_date > DATE_SUB( NOW(), INTERVAL 24 HOUR)")
    int countDailyTry(String phoneNo, String verifyType);

    @Query(nativeQuery = true,value= "SELECT * from verify_request v WHERE v.phone_no = :phoneNo and v.verify_type = :verifyType and v.verify_state = :verifyState and v.verify_date > DATE_SUB( NOW(), INTERVAL 36 HOUR) LIMIT 1 ")
    Optional<VerifyRequest> findVerify(@Param("phoneNo") String phoneNumber, @Param("verifyType")String verifyType, @Param("verifyState") String verifyState);

    @Modifying
    @Query(value="UPDATE VerifyRequest v  SET v.verifyState = :newVerifyState WHERE v.verifyState = :oldVerifyState and v.phoneNo =:phoneNo")
    int setVerifyState(String phoneNo, VerifyState oldVerifyState, VerifyState newVerifyState);

}