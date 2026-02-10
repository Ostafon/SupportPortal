package com.ostafon.supportportal.analytics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Response DTO for user analytics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnalyticsResponse {

    @JsonProperty("period")
    private String period;

    @JsonProperty("newUsersCount")
    private long newUsersCount;

    @JsonProperty("activeUsersCount")
    private long activeUsersCount;

    @JsonProperty("totalUsersWithTickets")
    private long totalUsersWithTickets;

    @JsonProperty("avgTicketsPerUser")
    private Double avgTicketsPerUser;

    @JsonProperty("usersWithMultipleTickets")
    private long usersWithMultipleTickets; // с 5+ тикетами

    @JsonProperty("userRetentionRate")
    private Double userRetentionRate; // percentage
}

