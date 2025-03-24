package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.OffDay;
import com.silverwing.dorothy.domain.entity.OffDayId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffDayRepository extends JpaRepository<OffDay, OffDayId> {
}
