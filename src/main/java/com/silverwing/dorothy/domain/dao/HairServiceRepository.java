package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.HairServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HairServiceRepository extends JpaRepository<HairServices, Integer>{
    @Query("SELECT s from HairServices s WHERE s.isUse = true order by s.idx")
    Optional<List<HairServices>> getAvailableServices();

    @Query("SELECT s from HairServices s where s.serviceId in :ids and s.isUse = true")
    Optional<List<HairServices>> findHairServicesByIds(List<Integer> ids);
}
