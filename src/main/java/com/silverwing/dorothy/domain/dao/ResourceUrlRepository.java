package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.ResourceUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceUrlRepository extends JpaRepository<ResourceUrl, Integer>, JpaSpecificationExecutor<ResourceUrl> {
}