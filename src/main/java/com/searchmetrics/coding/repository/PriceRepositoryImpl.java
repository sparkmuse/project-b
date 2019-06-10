package com.searchmetrics.coding.repository;

import com.searchmetrics.coding.entity.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceRepositoryImpl implements PriceRepositoryCustom{

    private static final String CREATED_FIELD = "created";

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Price> findLatest() {

        Sort sort = new Sort(Sort.Direction.DESC, CREATED_FIELD);
        Query query = new Query().with(sort).limit(1);

        Price price = mongoTemplate.findOne(query, Price.class);

        if (price != null) {
            return Optional.of(price);
        }

        return Optional.empty();
    }

    @Override
    public List<Price> priceInBetween(LocalDateTime start, LocalDateTime end) {

        Sort sort = new Sort(Sort.Direction.ASC, CREATED_FIELD);
        Criteria criteria = new Criteria().andOperator(
                        Criteria.where(CREATED_FIELD).gte(start),
                        Criteria.where(CREATED_FIELD).lte(end)
                );
        Query query = new Query().with(sort).addCriteria(criteria);

        return mongoTemplate.find(query, Price.class);
    }
}