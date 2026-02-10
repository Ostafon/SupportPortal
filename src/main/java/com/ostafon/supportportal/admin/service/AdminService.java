package com.ostafon.supportportal.admin.service;

import com.ostafon.supportportal.admin.dto.DashboardStatsResponse;
import com.ostafon.supportportal.admin.dto.EngineerPerformanceResponse;
import com.ostafon.supportportal.common.enums.TicketStatus;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import com.ostafon.supportportal.tickets.repo.TicketRepo;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for admin dashboard and statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepo userRepo;
    private final TicketRepo ticketRepo;

    /**
     * Get comprehensive dashboard statistics
     * @return dashboard statistics
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        log.info("Fetching dashboard statistics");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusWeeks(1);
        LocalDateTime startOfMonth = now.minusMonths(1);

        // User statistics
        long totalUsers = userRepo.count();
        long activeUsers = userRepo.countByIsActive(true);
        long inactiveUsers = userRepo.countByIsActive(false);
        long totalEngineers = userRepo.countByRole(UserRole.ENGINEER);
        long totalAdmins = userRepo.countByRole(UserRole.ADMIN);
        long newUsersToday = userRepo.countByCreatedAtAfter(startOfToday);
        long newUsersThisWeek = userRepo.countByCreatedAtAfter(startOfWeek);
        long newUsersThisMonth = userRepo.countByCreatedAtAfter(startOfMonth);

        // Ticket statistics
        long totalTickets = ticketRepo.count();
        long newTickets = ticketRepo.countByStatus(TicketStatus.NEW);
        long inProgressTickets = ticketRepo.countByStatus(TicketStatus.IN_PROGRESS);
        long resolvedTickets = ticketRepo.countByStatus(TicketStatus.RESOLVED);
        long closedTickets = ticketRepo.countByStatus(TicketStatus.CLOSED);
        long unassignedTickets = ticketRepo.countByAssigneeIsNull();

        long ticketsCreatedToday = ticketRepo.countByCreatedAtAfter(startOfToday);
        long ticketsCreatedThisWeek = ticketRepo.countByCreatedAtAfter(startOfWeek);
        long ticketsCreatedThisMonth = ticketRepo.countByCreatedAtAfter(startOfMonth);

        long ticketsResolvedToday = ticketRepo.countByStatusAndUpdatedAtAfter(TicketStatus.RESOLVED, startOfToday);
        long ticketsResolvedThisWeek = ticketRepo.countByStatusAndUpdatedAtAfter(TicketStatus.RESOLVED, startOfWeek);
        long ticketsResolvedThisMonth = ticketRepo.countByStatusAndUpdatedAtAfter(TicketStatus.RESOLVED, startOfMonth);

        // Performance metrics
        Double averageResolutionTimeHours = calculateAverageResolutionTime();
        Double ticketResolutionRate = calculateResolutionRate(totalTickets, resolvedTickets, closedTickets);

        // Engineer statistics
        long activeEngineers = userRepo.countByRoleAndIsActive(UserRole.ENGINEER, true);
        Double averageTicketsPerEngineer = activeEngineers > 0
            ? (double) totalTickets / activeEngineers
            : 0.0;

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .totalEngineers(totalEngineers)
                .totalAdmins(totalAdmins)
                .newUsersToday(newUsersToday)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersThisMonth(newUsersThisMonth)
                .totalTickets(totalTickets)
                .newTickets(newTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .unassignedTickets(unassignedTickets)
                .ticketsCreatedToday(ticketsCreatedToday)
                .ticketsCreatedThisWeek(ticketsCreatedThisWeek)
                .ticketsCreatedThisMonth(ticketsCreatedThisMonth)
                .ticketsResolvedToday(ticketsResolvedToday)
                .ticketsResolvedThisWeek(ticketsResolvedThisWeek)
                .ticketsResolvedThisMonth(ticketsResolvedThisMonth)
                .averageResolutionTimeHours(averageResolutionTimeHours)
                .ticketResolutionRate(ticketResolutionRate)
                .activeEngineers(activeEngineers)
                .averageTicketsPerEngineer(averageTicketsPerEngineer)
                .build();
    }

    /**
     * Get performance statistics for all engineers
     * @return list of engineer performance data
     */
    @Transactional(readOnly = true)
    public List<EngineerPerformanceResponse> getEngineerPerformance() {
        log.info("Fetching engineer performance statistics");

        List<UserEntity> engineers = userRepo.findByRole(UserRole.ENGINEER);
        List<EngineerPerformanceResponse> performanceList = new ArrayList<>();

        for (UserEntity engineer : engineers) {
            List<TicketEntity> assignedTickets = ticketRepo.findAllByAssigneeId(engineer.getId());

            long totalAssigned = assignedTickets.size();
            long active = assignedTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.NEW || t.getStatus() == TicketStatus.IN_PROGRESS)
                    .count();
            long resolved = assignedTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                    .count();
            long closed = assignedTickets.stream()
                    .filter(t -> t.getStatus() == TicketStatus.CLOSED)
                    .count();

            Double avgResolutionTime = calculateEngineerAverageResolutionTime(assignedTickets);
            Double resolutionRate = totalAssigned > 0
                ? ((double) (resolved + closed) / totalAssigned) * 100
                : 0.0;

            performanceList.add(EngineerPerformanceResponse.builder()
                    .engineerId(engineer.getId())
                    .engineerName(engineer.getFirstName() + " " + engineer.getLastName())
                    .engineerEmail(engineer.getEmail())
                    .totalAssignedTickets(totalAssigned)
                    .activeTickets(active)
                    .resolvedTickets(resolved)
                    .closedTickets(closed)
                    .averageResolutionTimeHours(avgResolutionTime)
                    .resolutionRate(resolutionRate)
                    .isActive(engineer.getIsActive())
                    .build());
        }

        // Sort by total assigned tickets (most busy engineers first)
        performanceList.sort((a, b) -> Long.compare(b.getTotalAssignedTickets(), a.getTotalAssignedTickets()));

        return performanceList;
    }

    /**
     * Calculate average ticket resolution time
     * @return average time in hours
     */
    private Double calculateAverageResolutionTime() {
        List<TicketEntity> resolvedTickets = ticketRepo.findAllByStatusIn(
                List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
        );

        if (resolvedTickets.isEmpty()) {
            return 0.0;
        }

        long totalHours = 0;
        int count = 0;

        for (TicketEntity ticket : resolvedTickets) {
            if (ticket.getClosedAt() != null) {
                Duration duration = Duration.between(ticket.getCreatedAt(), ticket.getClosedAt());
                totalHours += duration.toHours();
                count++;
            }
        }

        return count > 0 ? (double) totalHours / count : 0.0;
    }

    /**
     * Calculate average resolution time for specific engineer
     * @param tickets engineer's tickets
     * @return average time in hours
     */
    private Double calculateEngineerAverageResolutionTime(List<TicketEntity> tickets) {
        List<TicketEntity> resolvedTickets = tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED)
                .filter(t -> t.getClosedAt() != null)
                .toList();

        if (resolvedTickets.isEmpty()) {
            return 0.0;
        }

        long totalHours = resolvedTickets.stream()
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getClosedAt()).toHours())
                .sum();

        return (double) totalHours / resolvedTickets.size();
    }

    /**
     * Calculate ticket resolution rate
     * @param total total tickets
     * @param resolved resolved tickets
     * @param closed closed tickets
     * @return resolution rate percentage
     */
    private Double calculateResolutionRate(long total, long resolved, long closed) {
        if (total == 0) {
            return 0.0;
        }
        return ((double) (resolved + closed) / total) * 100;
    }

    /**
     * Get system health check
     * @return system health status
     */
    @Transactional(readOnly = true)
    public SystemHealthResponse getSystemHealth() {
        log.info("Checking system health");

        DashboardStatsResponse stats = getDashboardStats();

        // Check for potential issues
        boolean hasUnassignedTickets = stats.getUnassignedTickets() > 0;
        boolean hasInactiveEngineers = stats.getTotalEngineers() > stats.getActiveEngineers();
        boolean highTicketLoad = stats.getAverageTicketsPerEngineer() > 10; // threshold
        boolean slowResolution = stats.getAverageResolutionTimeHours() != null
                && stats.getAverageResolutionTimeHours() > 48; // 2 days threshold

        List<String> warnings = new ArrayList<>();
        if (hasUnassignedTickets) {
            warnings.add("There are " + stats.getUnassignedTickets() + " unassigned tickets");
        }
        if (hasInactiveEngineers) {
            warnings.add("Some engineers are inactive");
        }
        if (highTicketLoad) {
            warnings.add("High ticket load per engineer: " + String.format("%.1f", stats.getAverageTicketsPerEngineer()));
        }
        if (slowResolution) {
            warnings.add("Average resolution time is high: " + String.format("%.1f hours", stats.getAverageResolutionTimeHours()));
        }

        String status = warnings.isEmpty() ? "HEALTHY" : "WARNING";

        return SystemHealthResponse.builder()
                .status(status)
                .warnings(warnings)
                .totalUsers(stats.getTotalUsers())
                .activeUsers(stats.getActiveUsers())
                .totalTickets(stats.getTotalTickets())
                .unassignedTickets(stats.getUnassignedTickets())
                .activeEngineers(stats.getActiveEngineers())
                .averageTicketsPerEngineer(stats.getAverageTicketsPerEngineer())
                .averageResolutionTimeHours(stats.getAverageResolutionTimeHours())
                .build();
    }

    /**
     * System health response DTO
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SystemHealthResponse {
        private String status;
        private List<String> warnings;
        private long totalUsers;
        private long activeUsers;
        private long totalTickets;
        private long unassignedTickets;
        private long activeEngineers;
        private Double averageTicketsPerEngineer;
        private Double averageResolutionTimeHours;
    }
}

