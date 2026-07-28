package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.ServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceConfigRepository extends JpaRepository<ServiceConfig, String> {
}
