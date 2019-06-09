package com.searchmetrics.coding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.schedule")
@Getter
@Setter
public class ScheduleProperties {

    private Long fixedRateMilli;
}
