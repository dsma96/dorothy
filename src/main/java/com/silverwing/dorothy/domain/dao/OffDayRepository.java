package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.OffDay;
import com.silverwing.dorothy.domain.entity.OffDayId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OffDayRepository extends JpaRepository<OffDay, OffDayId> {

    @Query("SELECT o FROM com.silverwing.dorothy.domain.entity.OffDay o WHERE o.offDay >= :startDate and  o.offDay  < :endDate")
    Optional<List<OffDay>> getOffDays(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

 //   @Query("SELECT o FROM com.silverwing.dorothy.domain.entity.OffDay o WHERE o.offDay = :date and  o.desinger  = :desingerId")
    Optional<OffDay> findFirStByOffDayAndDesigner(@Param("date")Date offDay, @Param("designerId")int designer);
}
