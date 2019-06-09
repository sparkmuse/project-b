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
                .findTopByOrderByCreatedDesc()
                .orElseThrow(() -> new PriceNotFoundException("latest price not found in database"));
    }

    public List<Price> getHistoricalPrices(LocalDate start, LocalDate end) {

        LocalDateTime startOfDay = start.atTime(0, 0);
        LocalDateTime endOfDay = end.atTime(23, 59);

        if (end.isBefore(start)) {
            throw new InvalidDateRangeException("endDate " + end + " cannot be before startDate " + start);
        }

        List<Price> prices = priceRepository.findByCreatedBetween(startOfDay, endOfDay);

        if (prices.isEmpty()) {
            throw new PriceNotFoundException("latest price not found in database");
        }

        return prices;
    }
}
