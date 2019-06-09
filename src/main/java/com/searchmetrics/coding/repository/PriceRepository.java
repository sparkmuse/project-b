package com.searchmetrics.coding.repository;

import com.searchmetrics.coding.entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends MongoRepository<Price, String> {

    List<Price> findByCreatedBetweenOrderByCreatedDesc(LocalDateTime start, LocalDateTime end);

    Optional<Price> findTopByOrderByCreatedDesc();
}
