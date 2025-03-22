package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.reserve.OffDay;
import com.silverwing.dorothy.domain.reserve.OffDayId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffDayRepository extends JpaRepository<OffDay, OffDayId> {
}
