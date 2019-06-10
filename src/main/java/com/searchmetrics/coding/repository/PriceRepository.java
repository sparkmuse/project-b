package com.searchmetrics.coding.repository;

import com.searchmetrics.coding.entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends MongoRepository<Price, String>, PriceRepositoryCustom {

}
