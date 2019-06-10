package com.searchmetrics.coding.service;

import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.exception.InvalidDateRangeException;
import com.searchmetrics.coding.exception.PriceNotFoundException;
import com.searchmetrics.coding.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    public Price getLastPrice() {

        return priceRepository
                .findLatest()
                .orElseThrow(() -> new PriceNotFoundException("Latest price not found in database"));
    }

    public List<Price> getHistoricalPrices(String start, String end) {

        LocalDateTime startDay = LocalDate.parse(start).atTime(0,0);
        LocalDateTime endDay = LocalDate.parse(end).atTime(23, 59);

        if (endDay.isBefore(startDay)) {
            throw new InvalidDateRangeException("EndDate " + end + " cannot be before startDate " + start);
        }

        List<Price> prices = priceRepository.priceInBetween(startDay, endDay);

        if (prices.isEmpty()) {
            throw new PriceNotFoundException("Latest price not found in database");
        }

        return prices;
    }
}
