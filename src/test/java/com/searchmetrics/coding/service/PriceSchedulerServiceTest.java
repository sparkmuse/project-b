package com.searchmetrics.coding.service;

import com.searchmetrics.coding.client.CoinBaseClient;
import com.searchmetrics.coding.client.CoinBasePrice;
import com.searchmetrics.coding.client.Data;
import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceSchedulerServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CoinBaseClient coinBaseClient;

    private PriceSchedulerService priceSchedulerService;

    @BeforeEach
    void setUp() {
        this.priceSchedulerService = new PriceSchedulerService(priceRepository, coinBaseClient);
    }

    @Test
    @DisplayName("can save the price to the database")
    void savesPrice() {
        CoinBasePrice expected = new CoinBasePrice();
        Data data = Data.builder()
                .amount(7000.234d)
                .currency("USD")
                .build();
        expected.setData(data);

        when(coinBaseClient.getPrice()).thenReturn(Optional.of(expected));

        priceSchedulerService.fetchPrice();

        ArgumentCaptor<Price> captor = ArgumentCaptor.forClass(Price.class);
        verify(priceRepository).save(captor.capture());

        Price actual = captor.getValue();

        assertThat(actual.getRate()).isEqualTo(data.getAmount());
        assertThat(actual.getSymbol()).isEqualTo(data.getCurrency());
    }

    @Test
    @DisplayName("does not save anything to the database")
    void notPriceSaved() {

        when(coinBaseClient.getPrice()).thenReturn(Optional.empty());

        priceSchedulerService.fetchPrice();

        verifyZeroInteractions(priceRepository);
    }
}