package com.ostafon.supportportal.admin.controller;

import com.ostafon.supportportal.admin.dto.DashboardStatsResponse;
import com.ostafon.supportportal.admin.dto.EngineerPerformanceResponse;
import com.ostafon.supportportal.admin.service.AdminService;
import com.ostafon.supportportal.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for admin dashboard and system management
 * Provides comprehensive statistics, monitoring, and management endpoints
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Dashboard", description = "Admin panel and system management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * Get comprehensive dashboard statistics
     * Shows overview of users, tickets, and system performance
     */
    @GetMapping("/dashboard/stats")
    @Operation(
            summary = "Get dashboard statistics",
            description = "Retrieve comprehensive dashboard statistics including users, tickets, and performance metrics. Admin only."
    )
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        log.info("REST: Get dashboard statistics");

        DashboardStatsResponse stats = adminService.getDashboardStats();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get engineer performance statistics
     * Shows how each engineer is performing
     */
    @GetMapping("/engineers/performance")
    @Operation(
            summary = "Get engineer performance",
            description = "Retrieve performance statistics for all engineers including ticket counts and resolution times. Admin only."
    )
    public ResponseEntity<ApiResponse<List<EngineerPerformanceResponse>>> getEngineerPerformance() {
        log.info("REST: Get engineer performance statistics");

        List<EngineerPerformanceResponse> performance = adminService.getEngineerPerformance();

        return ResponseEntity.ok(ApiResponse.success(performance));
    }

    /**
     * Get system health check
     * Shows overall system health and warnings
     */
    @GetMapping("/system/health")
    @Operation(
            summary = "Get system health",
            description = "Check overall system health and get warnings about potential issues. Admin only."
    )
    public ResponseEntity<ApiResponse<AdminService.SystemHealthResponse>> getSystemHealth() {
        log.info("REST: Get system health check");

        AdminService.SystemHealthResponse health = adminService.getSystemHealth();

        return ResponseEntity.ok(ApiResponse.success(health));
    }
}

