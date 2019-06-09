package com.searchmetrics.coding.service;

import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.exception.InvalidDateRangeException;
import com.searchmetrics.coding.exception.PriceNotFoundException;
import com.searchmetrics.coding.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        this.priceService = new PriceService(priceRepository);
    }

    @Nested
    class LastPriceTest {

        @Test
        @DisplayName("returns price")
        void returnsLastPrice() {

            Price expected = Price.builder()
                    .id("my-id")
                    .rate(23.98d)
                    .created(LocalDateTime.parse("2019-06-09T10:20:00"))
                    .symbol("USD")
                    .build();

            when(priceRepository.findTopByOrderByCreatedDesc()).thenReturn(Optional.of(expected));

            Price lastPrice = priceService.getLastPrice();

            assertThat(lastPrice).isEqualToComparingFieldByField(expected);
        }

        @Test
        @DisplayName("throws exception when cannot find price")
        void priceNotFound() {

            when(priceRepository.findTopByOrderByCreatedDesc()).thenReturn(Optional.empty());

            assertThrows(PriceNotFoundException.class, () -> priceService.getLastPrice());
        }
    }

    @Nested
    class HistoricalPricesTest {

        @Test
        @DisplayName("returns prices")
        void returnsPrices() {

            Price expectedPrice = Price.builder()
                    .id("my-id")
                    .rate(23.98d)
                    .created(LocalDateTime.parse("2019-06-09T10:20:00"))
                    .symbol("USD")
                    .build();

            when(priceRepository.findByCreatedBetween(any(), any())).thenReturn(Collections.singletonList(expectedPrice));

            List<Price> prices = priceService.getHistoricalPrices(LocalDate.now(), LocalDate.now());

            assertThat(prices).isNotEmpty();
            assertThat(prices).containsExactly(expectedPrice);
        }

        @Test
        @DisplayName("throws exception when cannot find prices")
        void pricesNotFound() {

            when(priceRepository.findByCreatedBetween(any(), any())).thenReturn(Collections.emptyList());

            assertThrows(PriceNotFoundException.class,
                    () -> priceService.getHistoricalPrices(LocalDate.now(), LocalDate.now()));
        }

        @Test
        @DisplayName("throws exception when endDate > startDate")
        void datesException() {

            LocalDate startDate = LocalDate.parse("2019-06-08");
            LocalDate endDate = LocalDate.parse("2019-06-09");

            assertThrows(InvalidDateRangeException.class,
                    () -> priceService.getHistoricalPrices(endDate, startDate));
        }
    }
}