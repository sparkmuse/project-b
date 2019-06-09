package com.searchmetrics.coding.api;

import com.searchmetrics.coding.entity.Price;
import com.searchmetrics.coding.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = "latest")
    public Price getLastPrice(@RequestParam Boolean latest) {
        return priceService.getLastPrice();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = {"startDate", "endDate"})
    public List<Price> getHistoricalPrices(@RequestParam String startDate,
                                           @RequestParam String endDate) {
        return priceService.getHistoricalPrices(startDate, endDate);
    }
}
