package com.searchmetrics.coding.api;

import com.searchmetrics.coding.entity.Price;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
public class PriceController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Price getLastPrice(@RequestParam Boolean latest) {

        return Price.builder().rate(23.00d).build();
    }
}
