package com.searchmetrics.coding.service;

import com.searchmetrics.coding.client.CoinBaseClient;
import com.searchmetrics.coding.client.CoinBasePrice;
import com.searchmetrics.coding.client.Data;
import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceSchedulerService {

    public final PriceRepository priceRepository;
    public final CoinBaseClient coinBaseClient;

    @Scheduled(fixedDelayString = "${app.schedule.fixed-rate-milli}")
    public void fetchPrice() {

        Optional<CoinBasePrice> coinBasePrice = coinBaseClient.getPrice();

        if (coinBasePrice.isPresent()) {
            if (isValid(coinBasePrice.get())) {
                Data data = coinBasePrice.get().getData();
                Price price = Price.builder()
                        .created(LocalDateTime.now())
                        .rate(data.getAmount())
                        .symbol(data.getCurrency())
                        .build();
                priceRepository.save(price);
            }
        }
    }

    private static boolean isValid(CoinBasePrice price) {
        if (price.getData() == null) {
            log.info("data object is null {}", price);
            return false;
        }

        if (price.getData().getAmount() == null) {
            log.info("price  is null {}", price);
            return false;
        }

        return true;
    }
}
