package com.searchmetrics.coding.repository;

import com.searchmetrics.coding.entity.Price;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepositoryCustom {

    Optional<Price> findLatest();

    List<Price> priceInBetween(LocalDateTime start, LocalDateTime end);
}
