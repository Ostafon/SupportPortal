package com.ostafon.supportportal.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for ticket analytics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAnalyticsResponse {

    @JsonProperty("period")
    private String period; // THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("ticketMetrics")
    private TicketMetrics ticketMetrics;

    @JsonProperty("distributionByStatus")
    private Map<String, Long> distributionByStatus;

    @JsonProperty("distributionByPriority")
    private Map<String, Long> distributionByPriority;

    @JsonProperty("peakDays")
    private List<String> peakDays;

    @JsonProperty("peakHours")
    private List<Integer> peakHours;

    /**
     * Ticket metrics nested DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketMetrics {
        @JsonProperty("totalCreated")
        private long totalCreated;

        @JsonProperty("totalResolved")
        private long totalResolved;

        @JsonProperty("totalClosed")
        private long totalClosed;

        @JsonProperty("resolutionRate")
        private Double resolutionRate; // percentage

        @JsonProperty("avgResolutionTimeHours")
        private Double avgResolutionTimeHours;

        @JsonProperty("medianResolutionTimeHours")
        private Double medianResolutionTimeHours;

        @JsonProperty("avgFirstResponseTimeMinutes")
        private Double avgFirstResponseTimeMinutes;
    }
}

