package com.silverwing.dorothy.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resources_urls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String category1;

    @Column(nullable = false, length = 50)
    private String category2;

    @Column(length = 50)
    private String category3;

    @Column(name = "resource_name", nullable = false, length = 64)
    private String resourceName;

    @Column(nullable = false, length = 512)
    private String url;

    @Column(nullable = false, length = 512, name="thumbnail_url")
    private String thumbnailUrl;


    @Column(name = "useYN", nullable = false)
    private boolean useYN;

    @Column(name="value1")
    private String value1;
    @Column(name="value2")
    private String value2;

    @Column(name = "available_from", nullable = false)
    private LocalDateTime availableFrom;

    @Column(name = "available_to", nullable = false)
    private LocalDateTime availableTo;
}