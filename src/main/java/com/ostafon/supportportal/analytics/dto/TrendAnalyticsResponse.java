package com.ostafon.supportportal.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Response DTO for trend analysis
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendAnalyticsResponse {

    @JsonProperty("period")
    private String period;

    @JsonProperty("metric")
    private String metric; // TICKETS, RESOLUTION_TIME, ENGINEER_LOAD

    @JsonProperty("data")
    private List<TrendDataPoint> data;

    /**
     * Single data point in trend
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendDataPoint {
        @JsonProperty("date")
        private String date;

        @JsonProperty("value")
        private Double value;

        @JsonProperty("label")
        private String label; // e.g., "12 tickets"
    }
}

