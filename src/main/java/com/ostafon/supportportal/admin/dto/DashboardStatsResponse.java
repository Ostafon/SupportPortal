package com.ostafon.supportportal.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Dashboard statistics response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

    // User statistics
    @JsonProperty("totalUsers")
    private long totalUsers;

    @JsonProperty("activeUsers")
    private long activeUsers;

    @JsonProperty("inactiveUsers")
    private long inactiveUsers;

    @JsonProperty("totalEngineers")
    private long totalEngineers;

    @JsonProperty("totalAdmins")
    private long totalAdmins;

    @JsonProperty("newUsersToday")
    private long newUsersToday;

    @JsonProperty("newUsersThisWeek")
    private long newUsersThisWeek;

    @JsonProperty("newUsersThisMonth")
    private long newUsersThisMonth;

    // Ticket statistics
    @JsonProperty("totalTickets")
    private long totalTickets;

    @JsonProperty("newTickets")
    private long newTickets;

    @JsonProperty("inProgressTickets")
    private long inProgressTickets;

    @JsonProperty("resolvedTickets")
    private long resolvedTickets;

    @JsonProperty("closedTickets")
    private long closedTickets;

    @JsonProperty("unassignedTickets")
    private long unassignedTickets;

    @JsonProperty("ticketsCreatedToday")
    private long ticketsCreatedToday;

    @JsonProperty("ticketsCreatedThisWeek")
    private long ticketsCreatedThisWeek;

    @JsonProperty("ticketsCreatedThisMonth")
    private long ticketsCreatedThisMonth;

    @JsonProperty("ticketsResolvedToday")
    private long ticketsResolvedToday;

    @JsonProperty("ticketsResolvedThisWeek")
    private long ticketsResolvedThisWeek;

    @JsonProperty("ticketsResolvedThisMonth")
    private long ticketsResolvedThisMonth;

    // Performance metrics
    @JsonProperty("averageResolutionTimeHours")
    private Double averageResolutionTimeHours;

    @JsonProperty("ticketResolutionRate")
    private Double ticketResolutionRate; // percentage

    // Engineer statistics
    @JsonProperty("activeEngineers")
    private long activeEngineers;

    @JsonProperty("averageTicketsPerEngineer")
    private Double averageTicketsPerEngineer;
}

