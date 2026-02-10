package com.ostafon.supportportal.analytics.controller;

import com.ostafon.supportportal.analytics.dto.*;
import com.ostafon.supportportal.analytics.service.AnalyticsService;
import com.ostafon.supportportal.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for analytics
 * Provides insights on tickets, engineers, and users
 * Accessible to ADMIN (all data) and ENGINEER (own data)
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "System analytics and insights")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get ticket analytics for period
     */
    @GetMapping("/tickets")
    @Operation(
            summary = "Get ticket analytics",
            description = "Get comprehensive ticket metrics for selected period. " +
                         "Shows creation counts, resolution rates, distributions by status and priority."
    )
    public ResponseEntity<ApiResponse<TicketAnalyticsResponse>> getTicketAnalytics(
            @RequestParam(defaultValue = "THIS_MONTH")
            @Parameter(description = "Period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period) {

        log.info("REST: Get ticket analytics for period: {}", period);

        TicketAnalyticsResponse analytics = analyticsService.getTicketAnalytics(period);

        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    /**
     * Get engineer analytics for period
     */
    @GetMapping("/engineers")
    @Operation(
            summary = "Get engineer analytics",
            description = "Get performance metrics for engineers. " +
                         "Admin sees all engineers, engineers see only themselves. " +
                         "Sorted by resolution rate (effectiveness)."
    )
    public ResponseEntity<ApiResponse<EngineerAnalyticsResponse>> getEngineerAnalytics(
            @RequestParam(defaultValue = "THIS_MONTH")
            @Parameter(description = "Period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period) {

        log.info("REST: Get engineer analytics for period: {}", period);

        EngineerAnalyticsResponse analytics = analyticsService.getEngineerAnalytics(period);

        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    /**
     * Get user analytics for period
     */
    @GetMapping("/users")
    @Operation(
            summary = "Get user analytics",
            description = "Get user-related metrics: new users, active users, retention rate. " +
                         "Admin only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserAnalyticsResponse>> getUserAnalytics(
            @RequestParam(defaultValue = "THIS_MONTH")
            @Parameter(description = "Period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period) {

        log.info("REST: Get user analytics for period: {}", period);

        UserAnalyticsResponse analytics = analyticsService.getUserAnalytics(period);

        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    /**
     * Get trend analysis
     */
    @GetMapping("/trends")
    @Operation(
            summary = "Get trend analysis",
            description = "Get daily trend data for selected metric. " +
                         "Metrics: TICKETS (tickets created per day), " +
                         "RESOLUTION_TIME (avg resolution time per day), " +
                         "ENGINEER_LOAD (avg tickets per engineer per day)."
    )
    public ResponseEntity<ApiResponse<TrendAnalyticsResponse>> getTrends(
            @RequestParam(defaultValue = "TICKETS")
            @Parameter(description = "Metric: TICKETS, RESOLUTION_TIME, ENGINEER_LOAD")
            String metric,

            @RequestParam(defaultValue = "THIS_MONTH")
            @Parameter(description = "Period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period) {

        log.info("REST: Get trends for metric: {}, period: {}", metric, period);

        TrendAnalyticsResponse trends = analyticsService.getTrends(metric, period);

        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    /**
     * Compare two periods
     */
    @GetMapping("/compare")
    @Operation(
            summary = "Compare two periods",
            description = "Compare analytics between two periods to see improvements/degradation. " +
                         "Supported: month vs month, quarter vs quarter."
    )
    public ResponseEntity<ApiResponse<ComparisonAnalyticsResponse>> compareAnalytics(
            @RequestParam(defaultValue = "THIS_MONTH")
            @Parameter(description = "First period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period1,

            @RequestParam(defaultValue = "LAST_MONTH")
            @Parameter(description = "Second period: THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER")
            String period2) {

        log.info("REST: Compare analytics for periods: {} vs {}", period1, period2);

        ComparisonAnalyticsResponse comparison = analyticsService.compareAnalytics(period1, period2);

        return ResponseEntity.ok(ApiResponse.success(comparison));
    }
}

