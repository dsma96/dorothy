package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.api.dto.ResourceUrlResponse;
import com.silverwing.dorothy.domain.dao.ResourceUrlRepository;
import com.silverwing.dorothy.domain.entity.ResourceUrl;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceUrlService {

    private final ResourceUrlRepository repository;

    @Cacheable(value = "resources", key = "{#c1, #c2, #c3, #useYN, #from, #to}")
    public List<ResourceUrlResponse> getResources(String c1, String c2, String c3,
                                                  Boolean useYN, LocalDateTime from, LocalDateTime to) {

        Specification<ResourceUrl> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c1 != null) predicates.add(cb.equal(root.get("category1"), c1));
            if (c2 != null) predicates.add(cb.equal(root.get("category2"), c2));
            if (c3 != null) predicates.add(cb.equal(root.get("category3"), c3));
            if (useYN != null) predicates.add(cb.equal(root.get("useYN"), useYN));

            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("availableFrom"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("availableTo"), to));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ResourceUrlResponse convertToResponse(ResourceUrl entity) {
        return new ResourceUrlResponse(
                entity.getId(), entity.getCategory1(), entity.getCategory2(), entity.getCategory3(),
                entity.getResourceName(), entity.getUrl(), entity.isUseYN(),
                entity.getValue1(), entity.getValue2(), entity.getAvailableFrom(), entity.getAvailableTo(),
                entity.getThumbnailUrl()
        );
    }
}