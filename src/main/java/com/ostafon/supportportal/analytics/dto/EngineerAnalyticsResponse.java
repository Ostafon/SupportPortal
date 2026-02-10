package com.ostafon.supportportal.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Response DTO for engineer analytics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerAnalyticsResponse {

    @JsonProperty("period")
    private String period;

    @JsonProperty("engineers")
    private List<EngineerMetrics> engineers;

    /**
     * Individual engineer metrics
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EngineerMetrics {
        @JsonProperty("engineerId")
        private Long engineerId;

        @JsonProperty("engineerName")
        private String engineerName;

        @JsonProperty("engineerEmail")
        private String engineerEmail;

        @JsonProperty("rank")
        private Integer rank;

        @JsonProperty("totalTickets")
        private long totalTickets;

        @JsonProperty("resolvedTickets")
        private long resolvedTickets;

        @JsonProperty("closedTickets")
        private long closedTickets;

        @JsonProperty("resolutionRate")
        private Double resolutionRate; // percentage

        @JsonProperty("avgResolutionTimeHours")
        private Double avgResolutionTimeHours;

        @JsonProperty("avgFirstResponseTimeMinutes")
        private Double avgFirstResponseTimeMinutes;

        @JsonProperty("highPriorityCount")
        private long highPriorityCount;

        @JsonProperty("criticalPriorityCount")
        private long criticalPriorityCount;

        @JsonProperty("isActive")
        private Boolean isActive;
    }
}

