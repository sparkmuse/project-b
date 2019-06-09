package com.searchmetrics.coding.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class Price {

    @Id
    @Builder.Default
    private String id;

    private Double rate;

    private String symbol;

    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}