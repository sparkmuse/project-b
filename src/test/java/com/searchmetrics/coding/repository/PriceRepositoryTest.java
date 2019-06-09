package com.searchmetrics.coding.repository;

import com.searchmetrics.coding.entity.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class PriceRepositoryTest {

    @Autowired
    PriceRepository priceRepository;

    @Test
    @DisplayName("can find prices between start and end date")
    void datesInBetween() {

        Price price = Price.builder()
                .symbol("USD")
                .created(LocalDateTime.parse("2019-06-01T10:20:00"))
                .build();

        priceRepository.save(price);

        LocalDateTime start = LocalDateTime.parse("2019-06-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2019-06-05T23:59:00");

        List<Price> prices = priceRepository.findByCreatedBetween(start, end);

        assertThat(prices.get(0)).isEqualToComparingFieldByField(price);
    }
}