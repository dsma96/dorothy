package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.api.dto.ResourceUrlResponse;
import com.silverwing.dorothy.domain.dao.ResourceUrlRepository;
import com.silverwing.dorothy.domain.entity.ResourceUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceUrlServiceTest {

    @Mock
    private ResourceUrlRepository repository;

    @InjectMocks
    private ResourceUrlService resourceUrlService;

    @Test
    @DisplayName("Should correctly map ResourceUrl entity to ResourceUrlResponse DTO")
    void getResources_MappingTest() {
        // Given
        ResourceUrl entity = ResourceUrl.builder()
                .id(1)
                .category1("IMAGE")
                .category2("BANNER")
                .url("https://cdn.example.com/b.png")
                .useYN(true)
                .availableFrom(LocalDateTime.now())
                .availableTo(LocalDateTime.now().plusDays(7))
                .build();

        when(repository.findAll(any(Specification.class))).thenReturn(List.of(entity));

        // When
        List<ResourceUrlResponse> results = resourceUrlService.getResources(
                "IMAGE", null, null, null, null, null
        );

        // Then
        assertThat(results).hasSize(1);
        ResourceUrlResponse response = results.get(0);
        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.category1()).isEqualTo(entity.getCategory1());
        assertThat(response.url()).isEqualTo(entity.getUrl());
        assertThat(response.useYN()).isTrue();

        verify(repository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should return empty list when repository returns no results")
    void getResources_EmptyResultTest() {
        // Given
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());

        // When
        List<ResourceUrlResponse> results = resourceUrlService.getResources(
                null, null, null, null, null, null
        );

        // Then
        assertThat(results).isEmpty();
    }
}