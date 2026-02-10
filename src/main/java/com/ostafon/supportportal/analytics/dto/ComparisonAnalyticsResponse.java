package com.ostafon.supportportal.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * Response DTO for comparison analytics (month vs month, quarter vs quarter)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonAnalyticsResponse {

    @JsonProperty("period1")
    private String period1;

    @JsonProperty("period2")
    private String period2;

    @JsonProperty("comparison")
    private Map<String, ComparisonMetric> comparison;

    /**
     * Single comparison metric
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComparisonMetric {
        @JsonProperty("period1Value")
        private Double period1Value;

        @JsonProperty("period2Value")
        private Double period2Value;

        @JsonProperty("changePercent")
        private Double changePercent;

        @JsonProperty("trend")
        private String trend; // BETTER, WORSE, SAME

        @JsonProperty("label")
        private String label;
    }
}

