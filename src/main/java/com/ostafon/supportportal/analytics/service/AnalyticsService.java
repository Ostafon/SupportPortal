package com.ostafon.supportportal.analytics.service;

import com.ostafon.supportportal.analytics.dto.*;
import com.ostafon.supportportal.common.enums.TicketPriority;
import com.ostafon.supportportal.common.enums.TicketStatus;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import com.ostafon.supportportal.tickets.repo.TicketRepo;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for analytics calculations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final TicketRepo ticketRepo;
    private final UserRepo userRepo;

    /**
     * Get ticket analytics for period
     * @param period period (THIS_MONTH, LAST_MONTH, THIS_QUARTER, LAST_QUARTER)
     * @return ticket analytics
     */
    @Transactional(readOnly = true)
    public TicketAnalyticsResponse getTicketAnalytics(String period) {
        log.info("Fetching ticket analytics for period: {}", period);

        LocalDateTime[] dates = getPeriodDates(period);
        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];

        List<TicketEntity> tickets = ticketRepo.findAllByCreatedAtBetween(startDate, endDate);

        // Calculate metrics
        long totalCreated = tickets.size();
        long totalResolved = tickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count();
        long totalClosed = tickets.stream().filter(t -> t.getStatus() == TicketStatus.CLOSED).count();
        double resolutionRate = totalCreated > 0 ? ((double) (totalResolved + totalClosed) / totalCreated) * 100 : 0.0;

        // Average resolution time
        List<TicketEntity> resolvedTickets = tickets.stream()
                .filter(t -> (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED) && t.getClosedAt() != null)
                .collect(Collectors.toList());

        double avgResolutionTime = 0;
        double medianResolutionTime = 0;
        if (!resolvedTickets.isEmpty()) {
            List<Long> resolutionTimes = resolvedTickets.stream()
                    .map(t -> Duration.between(t.getCreatedAt(), t.getClosedAt()).toHours())
                    .collect(Collectors.toList());
            avgResolutionTime = resolutionTimes.stream().mapToLong(Long::longValue).average().orElse(0);

            resolutionTimes.sort(Long::compareTo);
            medianResolutionTime = resolutionTimes.get(resolutionTimes.size() / 2);
        }

        // Distribution by status
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("NEW", tickets.stream().filter(t -> t.getStatus() == TicketStatus.NEW).count());
        statusDistribution.put("IN_PROGRESS", tickets.stream().filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS).count());
        statusDistribution.put("RESOLVED", totalResolved);
        statusDistribution.put("CLOSED", totalClosed);

        // Distribution by priority
        Map<String, Long> priorityDistribution = new HashMap<>();
        priorityDistribution.put("LOW", tickets.stream().filter(t -> t.getPriority() == TicketPriority.LOW).count());
        priorityDistribution.put("MEDIUM", tickets.stream().filter(t -> t.getPriority() == TicketPriority.MEDIUM).count());
        priorityDistribution.put("HIGH", tickets.stream().filter(t -> t.getPriority() == TicketPriority.HIGH).count());
        priorityDistribution.put("CRITICAL", tickets.stream().filter(t -> t.getPriority() == TicketPriority.CRITICAL).count());

        // Peak days
        Map<String, Long> dayCount = new HashMap<>();
        tickets.forEach(t -> {
            String day = t.getCreatedAt().getDayOfWeek().toString();
            dayCount.put(day, dayCount.getOrDefault(day, 0L) + 1);
        });
        List<String> peakDays = dayCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Peak hours
        Map<Integer, Long> hourCount = new HashMap<>();
        tickets.forEach(t -> {
            int hour = t.getCreatedAt().getHour();
            hourCount.put(hour, hourCount.getOrDefault(hour, 0L) + 1);
        });
        List<Integer> peakHours = hourCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        TicketAnalyticsResponse.TicketMetrics metrics = TicketAnalyticsResponse.TicketMetrics.builder()
                .totalCreated(totalCreated)
                .totalResolved(totalResolved)
                .totalClosed(totalClosed)
                .resolutionRate(resolutionRate)
                .avgResolutionTimeHours(avgResolutionTime)
                .medianResolutionTimeHours(medianResolutionTime)
                .avgFirstResponseTimeMinutes(0.0) // TODO: implement when messages are ready
                .build();

        return TicketAnalyticsResponse.builder()
                .period(period)
                .startDate(startDate.toLocalDate().toString())
                .endDate(endDate.toLocalDate().toString())
                .ticketMetrics(metrics)
                .distributionByStatus(statusDistribution)
                .distributionByPriority(priorityDistribution)
                .peakDays(peakDays)
                .peakHours(peakHours)
                .build();
    }

    /**
     * Get engineer analytics
     * @param period period
     * @return engineer analytics
     */
    @Transactional(readOnly = true)
    public EngineerAnalyticsResponse getEngineerAnalytics(String period) {
        log.info("Fetching engineer analytics for period: {}", period);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasRole("ADMIN");

        LocalDateTime[] dates = getPeriodDates(period);
        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];

        List<UserEntity> engineers;
        if (isAdmin) {
            engineers = userRepo.findByRole(UserRole.ENGINEER);
        } else {
            // Engineer sees only themselves
            UserEntity user = userRepo.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
            engineers = List.of(user);
        }

        List<EngineerAnalyticsResponse.EngineerMetrics> engineerMetrics = new ArrayList<>();
        int rank = 1;

        // Sort by resolution rate
        List<EngineerAnalyticsResponse.EngineerMetrics> tempMetrics = engineers.stream()
                .map(engineer -> calculateEngineerMetrics(engineer, startDate, endDate))
                .collect(Collectors.toList());

        tempMetrics.sort((a, b) -> Double.compare(b.getResolutionRate(), a.getResolutionRate()));

        for (int i = 0; i < tempMetrics.size(); i++) {
            tempMetrics.get(i).setRank(i + 1);
            engineerMetrics.add(tempMetrics.get(i));
        }

        return EngineerAnalyticsResponse.builder()
                .period(period)
                .engineers(engineerMetrics)
                .build();
    }

    /**
     * Get user analytics
     * @param period period
     * @return user analytics
     */
    @Transactional(readOnly = true)
    public UserAnalyticsResponse getUserAnalytics(String period) {
        log.info("Fetching user analytics for period: {}", period);

        LocalDateTime[] dates = getPeriodDates(period);
        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];

        // Get previous period for retention calculation
        LocalDateTime[] prevDates = getPreviousPeriodDates(period);
        LocalDateTime prevStartDate = prevDates[0];
        LocalDateTime prevEndDate = prevDates[1];

        long newUsersCount = userRepo.countByCreatedAtBetween(startDate, endDate);

        List<UserEntity> allUsersInPeriod = userRepo.findByCreatedAtBefore(endDate);
        List<UserEntity> activeUsersThisPeriod = userRepo.findByCreatedAtBefore(endDate);

        long totalUsersWithTickets = ticketRepo.countDistinctRequesterIds(startDate, endDate);
        long avgTicketsPerUser = totalUsersWithTickets > 0 ? ticketRepo.count() / totalUsersWithTickets : 0;

        // Users with 5+ tickets
        List<Long> userTicketCounts = ticketRepo.findTicketCountsByRequester(startDate, endDate);
        long usersWithMultiple = userTicketCounts.stream().filter(count -> count >= 5).count();

        // Retention rate (users who had tickets in both periods)
        List<Long> usersLastPeriod = ticketRepo.findDistinctRequesterIds(prevStartDate, prevEndDate);
        List<Long> usersThisPeriod = ticketRepo.findDistinctRequesterIds(startDate, endDate);
        long retainedUsers = usersThisPeriod.stream().filter(usersLastPeriod::contains).count();
        double retentionRate = !usersLastPeriod.isEmpty() ? ((double) retainedUsers / usersLastPeriod.size()) * 100 : 0;

        return UserAnalyticsResponse.builder()
                .period(period)
                .newUsersCount(newUsersCount)
                .activeUsersCount(activeUsersThisPeriod.size())
                .totalUsersWithTickets(totalUsersWithTickets)
                .avgTicketsPerUser((double) avgTicketsPerUser)
                .usersWithMultipleTickets(usersWithMultiple)
                .userRetentionRate(retentionRate)
                .build();
    }

    /**
     * Get trend analytics
     * @param metric metric type (TICKETS, RESOLUTION_TIME, ENGINEER_LOAD)
     * @param period period
     * @return trend data
     */
    @Transactional(readOnly = true)
    public TrendAnalyticsResponse getTrends(String metric, String period) {
        log.info("Fetching trend analytics for metric: {}, period: {}", metric, period);

        LocalDateTime[] dates = getPeriodDates(period);
        LocalDateTime startDate = dates[0];
        LocalDateTime endDate = dates[1];

        List<TrendAnalyticsResponse.TrendDataPoint> data = new ArrayList<>();

        if ("TICKETS".equals(metric)) {
            data = calculateTicketsTrend(startDate, endDate);
        } else if ("RESOLUTION_TIME".equals(metric)) {
            data = calculateResolutionTimeTrend(startDate, endDate);
        } else if ("ENGINEER_LOAD".equals(metric)) {
            data = calculateEngineerLoadTrend(startDate, endDate);
        }

        return TrendAnalyticsResponse.builder()
                .period(period)
                .metric(metric)
                .data(data)
                .build();
    }

    /**
     * Compare two periods
     * @param period1 first period
     * @param period2 second period
     * @return comparison data
     */
    @Transactional(readOnly = true)
    public ComparisonAnalyticsResponse compareAnalytics(String period1, String period2) {
        log.info("Comparing analytics for periods: {} vs {}", period1, period2);

        TicketAnalyticsResponse stats1 = getTicketAnalytics(period1);
        TicketAnalyticsResponse stats2 = getTicketAnalytics(period2);

        Map<String, ComparisonAnalyticsResponse.ComparisonMetric> comparison = new HashMap<>();

        // Compare total tickets
        double change1 = calculateChangePercent(
                stats2.getTicketMetrics().getTotalCreated(),
                stats1.getTicketMetrics().getTotalCreated()
        );
        comparison.put("totalTickets", ComparisonAnalyticsResponse.ComparisonMetric.builder()
                .period1Value((double) stats1.getTicketMetrics().getTotalCreated())
                .period2Value((double) stats2.getTicketMetrics().getTotalCreated())
                .changePercent(change1)
                .trend(change1 < 0 ? "BETTER" : change1 > 0 ? "WORSE" : "SAME")
                .label("Total Tickets")
                .build());

        // Compare resolution rate
        double change2 = calculateChangePercent(
                stats2.getTicketMetrics().getResolutionRate(),
                stats1.getTicketMetrics().getResolutionRate()
        );
        comparison.put("resolutionRate", ComparisonAnalyticsResponse.ComparisonMetric.builder()
                .period1Value(stats1.getTicketMetrics().getResolutionRate())
                .period2Value(stats2.getTicketMetrics().getResolutionRate())
                .changePercent(change2)
                .trend(change2 > 0 ? "BETTER" : change2 < 0 ? "WORSE" : "SAME")
                .label("Resolution Rate (%)")
                .build());

        // Compare avg resolution time
        double change3 = calculateChangePercent(
                stats2.getTicketMetrics().getAvgResolutionTimeHours(),
                stats1.getTicketMetrics().getAvgResolutionTimeHours()
        );
        comparison.put("avgResolutionTime", ComparisonAnalyticsResponse.ComparisonMetric.builder()
                .period1Value(stats1.getTicketMetrics().getAvgResolutionTimeHours())
                .period2Value(stats2.getTicketMetrics().getAvgResolutionTimeHours())
                .changePercent(change3)
                .trend(change3 < 0 ? "BETTER" : change3 > 0 ? "WORSE" : "SAME")
                .label("Avg Resolution Time (hours)")
                .build());

        return ComparisonAnalyticsResponse.builder()
                .period1(period1)
                .period2(period2)
                .comparison(comparison)
                .build();
    }

    // ==================== Helper Methods ====================

    private LocalDateTime[] getPeriodDates(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        return switch (period) {
            case "THIS_MONTH" -> new LocalDateTime[]{
                    today.withDayOfMonth(1).atStartOfDay(),
                    now
            };
            case "LAST_MONTH" -> {
                YearMonth lastMonth = YearMonth.now().minusMonths(1);
                yield new LocalDateTime[]{
                        lastMonth.atDay(1).atStartOfDay(),
                        lastMonth.atEndOfMonth().atTime(23, 59, 59)
                };
            }
            case "THIS_QUARTER" -> {
                int month = today.getMonthValue();
                int quarterStart = ((month - 1) / 3) * 3 + 1;
                LocalDate quarterStartDate = today.withMonth(quarterStart).withDayOfMonth(1);
                yield new LocalDateTime[]{
                        quarterStartDate.atStartOfDay(),
                        now
                };
            }
            case "LAST_QUARTER" -> {
                int month = today.getMonthValue();
                int lastQuarterStart = ((month - 1) / 3 - 1) * 3 + 1;
                LocalDate quarterStartDate = today.withMonth(lastQuarterStart).withDayOfMonth(1);
                LocalDate quarterEndDate = quarterStartDate.plusMonths(3).minusDays(1);
                yield new LocalDateTime[]{
                        quarterStartDate.atStartOfDay(),
                        quarterEndDate.atTime(23, 59, 59)
                };
            }
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private LocalDateTime[] getPreviousPeriodDates(String period) {
        return switch (period) {
            case "THIS_MONTH" -> getPeriodDates("LAST_MONTH");
            case "LAST_MONTH" -> {
                YearMonth lastMonth = YearMonth.now().minusMonths(2);
                yield new LocalDateTime[]{
                        lastMonth.atDay(1).atStartOfDay(),
                        lastMonth.atEndOfMonth().atTime(23, 59, 59)
                };
            }
            case "THIS_QUARTER" -> getPeriodDates("LAST_QUARTER");
            case "LAST_QUARTER" -> {
                int month = LocalDate.now().getMonthValue();
                int twoQuartersAgoStart = ((month - 1) / 3 - 2) * 3 + 1;
                LocalDate quarterStartDate = LocalDate.now().withMonth(twoQuartersAgoStart).withDayOfMonth(1);
                LocalDate quarterEndDate = quarterStartDate.plusMonths(3).minusDays(1);
                yield new LocalDateTime[]{
                        quarterStartDate.atStartOfDay(),
                        quarterEndDate.atTime(23, 59, 59)
                };
            }
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private EngineerAnalyticsResponse.EngineerMetrics calculateEngineerMetrics(
            UserEntity engineer, LocalDateTime startDate, LocalDateTime endDate) {

        List<TicketEntity> assignedTickets = ticketRepo.findAllByAssigneeIdAndCreatedAtBetween(
                engineer.getId(), startDate, endDate
        );

        long totalTickets = assignedTickets.size();
        long resolvedTickets = assignedTickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count();
        long closedTickets = assignedTickets.stream().filter(t -> t.getStatus() == TicketStatus.CLOSED).count();
        double resolutionRate = totalTickets > 0 ? ((double) (resolvedTickets + closedTickets) / totalTickets) * 100 : 0.0;

        double avgResolutionTime = 0;
        if (!assignedTickets.isEmpty()) {
            List<TicketEntity> resolvedList = assignedTickets.stream()
                    .filter(t -> t.getClosedAt() != null && (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED))
                    .collect(Collectors.toList());
            if (!resolvedList.isEmpty()) {
                avgResolutionTime = resolvedList.stream()
                        .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getClosedAt()).toHours())
                        .average()
                        .orElse(0);
            }
        }

        long highPriorityCount = assignedTickets.stream().filter(t -> t.getPriority() == TicketPriority.HIGH).count();
        long criticalCount = assignedTickets.stream().filter(t -> t.getPriority() == TicketPriority.CRITICAL).count();

        return EngineerAnalyticsResponse.EngineerMetrics.builder()
                .engineerId(engineer.getId())
                .engineerName(engineer.getFirstName() + " " + engineer.getLastName())
                .engineerEmail(engineer.getEmail())
                .totalTickets(totalTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .resolutionRate(resolutionRate)
                .avgResolutionTimeHours(avgResolutionTime)
                .avgFirstResponseTimeMinutes(0.0) // TODO: when messages ready
                .highPriorityCount(highPriorityCount)
                .criticalPriorityCount(criticalCount)
                .isActive(engineer.getIsActive())
                .build();
    }

    private List<TrendAnalyticsResponse.TrendDataPoint> calculateTicketsTrend(
            LocalDateTime startDate, LocalDateTime endDate) {
        List<TrendAnalyticsResponse.TrendDataPoint> data = new ArrayList<>();

        LocalDate current = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(23, 59, 59);

            long count = ticketRepo.countByCreatedAtBetween(dayStart, dayEnd);

            data.add(TrendAnalyticsResponse.TrendDataPoint.builder()
                    .date(current.toString())
                    .value((double) count)
                    .label(count + " tickets")
                    .build());

            current = current.plusDays(1);
        }

        return data;
    }

    private List<TrendAnalyticsResponse.TrendDataPoint> calculateResolutionTimeTrend(
            LocalDateTime startDate, LocalDateTime endDate) {
        List<TrendAnalyticsResponse.TrendDataPoint> data = new ArrayList<>();

        LocalDate current = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(23, 59, 59);

            List<TicketEntity> resolvedToday = ticketRepo.findAllByStatusInAndClosedAtBetween(
                    List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED),
                    dayStart, dayEnd
            );

            double avgTime = 0;
            if (!resolvedToday.isEmpty()) {
                avgTime = resolvedToday.stream()
                        .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getClosedAt()).toHours())
                        .average()
                        .orElse(0);
            }

            data.add(TrendAnalyticsResponse.TrendDataPoint.builder()
                    .date(current.toString())
                    .value(avgTime)
                    .label(String.format("%.1f hours", avgTime))
                    .build());

            current = current.plusDays(1);
        }

        return data;
    }

    private List<TrendAnalyticsResponse.TrendDataPoint> calculateEngineerLoadTrend(
            LocalDateTime startDate, LocalDateTime endDate) {
        List<TrendAnalyticsResponse.TrendDataPoint> data = new ArrayList<>();

        LocalDate current = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(23, 59, 59);

            long activeTickets = ticketRepo.countByStatusNotInAndCreatedAtBefore(
                    List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED),
                    dayEnd
            );

            List<UserEntity> activeEngineers = userRepo.findByRoleAndIsActive(UserRole.ENGINEER, true);
            double avgLoad = !activeEngineers.isEmpty() ? (double) activeTickets / activeEngineers.size() : 0;

            data.add(TrendAnalyticsResponse.TrendDataPoint.builder()
                    .date(current.toString())
                    .value(avgLoad)
                    .label(String.format("%.1f tickets/engineer", avgLoad))
                    .build());

            current = current.plusDays(1);
        }

        return data;
    }

    private double calculateChangePercent(double newValue, double oldValue) {
        if (oldValue == 0) return newValue > 0 ? 100 : 0;
        return ((newValue - oldValue) / oldValue) * 100;
    }
}

