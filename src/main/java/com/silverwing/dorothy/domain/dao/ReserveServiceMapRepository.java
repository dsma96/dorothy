package com.silverwing.dorothy.domain.dao;
import com.silverwing.dorothy.domain.entity.ReserveServiceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReserveServiceMapRepository extends JpaRepository<ReserveServiceMap, Integer> {
    @Modifying
    @Query( value=" DELETE FROM ReserveServiceMap M where M.regId = :regId")
    int deleteByRegId(@Param("regId") int regId);
}
