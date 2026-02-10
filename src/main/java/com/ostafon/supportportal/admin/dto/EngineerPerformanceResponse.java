package com.ostafon.supportportal.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Engineer performance statistics DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerPerformanceResponse {

    @JsonProperty("engineerId")
    private Long engineerId;

    @JsonProperty("engineerName")
    private String engineerName;

    @JsonProperty("engineerEmail")
    private String engineerEmail;

    @JsonProperty("totalAssignedTickets")
    private long totalAssignedTickets;

    @JsonProperty("activeTickets")
    private long activeTickets;

    @JsonProperty("resolvedTickets")
    private long resolvedTickets;

    @JsonProperty("closedTickets")
    private long closedTickets;

    @JsonProperty("averageResolutionTimeHours")
    private Double averageResolutionTimeHours;

    @JsonProperty("resolutionRate")
    private Double resolutionRate; // percentage

    @JsonProperty("isActive")
    private Boolean isActive;
}

