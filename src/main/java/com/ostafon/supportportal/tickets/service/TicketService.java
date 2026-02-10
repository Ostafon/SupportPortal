package com.ostafon.supportportal.tickets.service;

import com.ostafon.supportportal.common.enums.TicketStatus;
import com.ostafon.supportportal.common.enums.UserRole;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.notifications.service.NotificationService;
import com.ostafon.supportportal.tickets.dto.request.CreateTicketRequest;
import com.ostafon.supportportal.tickets.dto.request.UpdateTicketRequest;
import com.ostafon.supportportal.tickets.dto.response.TicketResponse;
import com.ostafon.supportportal.tickets.mapper.TicketMapper;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import com.ostafon.supportportal.tickets.repo.TicketRepo;
import com.ostafon.supportportal.users.model.EngineerGroupEntity;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.EngineerGroupRepo;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for ticket management operations
 * Handles CRUD operations, assignment, and status management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepo ticketRepo;
    private final UserRepo userRepo;
    private final EngineerGroupRepo groupRepo;
    private final NotificationService notificationService;

    /**
     * Create a new ticket
     * @param request create ticket request
     * @return created ticket DTO
     */
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.info("Creating new ticket by user {}: {}", currentUserId, request.getTitle());

        // Get current user as requester
        UserEntity requester = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        // Build ticket entity
        TicketEntity ticket = TicketEntity.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .priority(request.getPriority())
                .status(TicketStatus.NEW)
                .requester(requester)
                .dueAt(request.getDueAt())
                .build();

        // Only admins can assign on creation
        if (request.getAssigneeId() != null) {
            if (!SecurityUtils.hasRole("ADMIN")) {
                log.warn("User {} tried to assign ticket on creation", currentUserId);
                throw new AccessDeniedException("Only admins can assign tickets on creation");
            }

            UserEntity assignee = userRepo.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee", "id", request.getAssigneeId()));
            ticket.setAssignee(assignee);
        }

        // Only admins can set group on creation
        if (request.getGroupId() != null) {
            if (!SecurityUtils.hasRole("ADMIN")) {
                log.warn("User {} tried to set group on creation", currentUserId);
                throw new AccessDeniedException("Only admins can set ticket group on creation");
            }

            EngineerGroupEntity group = groupRepo.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
            ticket.setGroup(group);
        }

        ticket = ticketRepo.save(ticket);
        log.info("Ticket created successfully with ID: {}", ticket.getId());

        // Notify all engineers about new ticket (in-app)
        notificationService.notifyUsersInApp(
                userRepo.findByRole(com.ostafon.supportportal.common.enums.UserRole.ENGINEER),
                "New ticket created",
                "Ticket #" + ticket.getId() + ": " + ticket.getTitle()
        );

        // Notify all engineers about new ticket (email)
        notificationService.notifyUsersEmail(
                userRepo.findByRole(com.ostafon.supportportal.common.enums.UserRole.ENGINEER),
                "New ticket created",
                "Ticket #" + ticket.getId() + ": " + ticket.getTitle()
        );

        // Notify requester (email)
        notificationService.notifyUserEmail(
                requester.getId(),
                "Your ticket was created",
                "Ticket #" + ticket.getId() + " has been created."
        );

        return TicketMapper.toResponse(ticket);
    }

    /**
     * Get ticket by ID
     * User can view their own tickets, agents/admins can view all
     * @param ticketId ticket ID
     * @return ticket DTO
     */
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long ticketId) {
        log.debug("Fetching ticket by ID: {}", ticketId);

        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        // Check access permissions
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasAnyRole("ADMIN", "ENGINEER");
        boolean isRequester = ticket.getRequester().getId().equals(currentUserId);
        boolean isAssignee = ticket.getAssignee() != null && ticket.getAssignee().getId().equals(currentUserId);

        if (!isAdmin && !isRequester && !isAssignee) {
            log.warn("Access denied: User {} tried to view ticket {}", currentUserId, ticketId);
            throw new AccessDeniedException("You don't have permission to view this ticket");
        }

        return TicketMapper.toResponse(ticket);
    }

    /**
     * Get all tickets with pagination
     * Users see their own tickets, agents/admins see all
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Transactional(readOnly = true)
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasAnyRole("ADMIN", "ENGINEER");

        Page<TicketEntity> tickets;

        if (isAdmin) {
            log.info("Admin/Engineer fetching all tickets");
            tickets = ticketRepo.findAllWithDetails(pageable);
        } else {
            log.info("User {} fetching their own tickets", currentUserId);
            tickets = ticketRepo.findByRequesterId(currentUserId, pageable);
        }

        return tickets.map(TicketMapper::toResponse);
    }

    /**
     * Get tickets by status
     * @param status ticket status
     * @param pageable pagination parameters
     * @return page of tickets
     */
    @Transactional(readOnly = true)
    public Page<TicketResponse> getTicketsByStatus(TicketStatus status, Pageable pageable) {
        log.info("Fetching tickets by status: {}", status);

        if (!SecurityUtils.hasAnyRole("ADMIN", "ENGINEER")) {
            throw new AccessDeniedException("Only engineers and admins can filter tickets by status");
        }

        Page<TicketEntity> tickets = ticketRepo.findByStatus(status, pageable);
        return tickets.map(TicketMapper::toResponse);
    }

    /**
     * Get tickets assigned to current user
     * @param pageable pagination parameters
     * @return page of assigned tickets
     */
    @Transactional(readOnly = true)
    public Page<TicketResponse> getMyAssignedTickets(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.info("Fetching tickets assigned to user {}", currentUserId);

        Page<TicketEntity> tickets = ticketRepo.findByAssigneeId(currentUserId, pageable);
        return tickets.map(TicketMapper::toResponse);
    }

    /**
     * Get unassigned tickets (admin/agent only)
     * @param pageable pagination parameters
     * @return page of unassigned tickets
     */
    @Transactional(readOnly = true)
    public Page<TicketResponse> getUnassignedTickets(Pageable pageable) {
        log.info("Fetching unassigned tickets");

        Page<TicketEntity> tickets = ticketRepo.findUnassignedTickets(pageable);
        return tickets.map(TicketMapper::toResponse);
    }

    /**
     * Update ticket
     * Users can update only their own tickets (limited fields)
     * Agents/admins can update any ticket
     * @param ticketId ticket ID
     * @param request update request
     * @return updated ticket DTO
     */
    @Transactional
    public TicketResponse updateTicket(Long ticketId, UpdateTicketRequest request) {
        log.info("Updating ticket ID: {}", ticketId);

        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.hasAnyRole("ADMIN", "ENGINEER");
        boolean isRequester = ticket.getRequester().getId().equals(currentUserId);

        // Check permissions
        if (!isAdmin && !isRequester) {
            log.warn("Access denied: User {} tried to update ticket {}", currentUserId, ticketId);
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        // Users can only update title and description
        if (!isAdmin) {
            if (request.getStatus() != null || request.getAssigneeId() != null ||
                request.getGroupId() != null) {
                log.warn("User {} tried to update restricted fields on ticket {}", currentUserId, ticketId);
                throw new AccessDeniedException("You can only update title and description");
            }
        }

        // Update fields
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            ticket.setTitle(request.getTitle().trim());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            ticket.setDescription(request.getDescription().trim());
        }

        if (isAdmin) {
            if (request.getPriority() != null) {
                ticket.setPriority(request.getPriority());
            }

            if (request.getStatus() != null) {
                TicketStatus oldStatus = ticket.getStatus();
                ticket.setStatus(request.getStatus());

                // Set closedAt when status changes to CLOSED or RESOLVED
                if ((request.getStatus() == TicketStatus.CLOSED ||
                     request.getStatus() == TicketStatus.RESOLVED) &&
                    ticket.getClosedAt() == null) {
                    ticket.setClosedAt(LocalDateTime.now());
                    log.info("Ticket {} closed/resolved at {}", ticketId, ticket.getClosedAt());

                    // Notify requester and assignee (email)
                    if (ticket.getRequester() != null) {
                        notificationService.notifyUserEmail(
                                ticket.getRequester().getId(),
                                "Your ticket status changed",
                                "Ticket #" + ticket.getId() + " status: " + request.getStatus()
                        );
                    }
                    if (ticket.getAssignee() != null) {
                        notificationService.notifyUserEmail(
                                ticket.getAssignee().getId(),
                                "Assigned ticket status changed",
                                "Ticket #" + ticket.getId() + " status: " + request.getStatus()
                        );
                    }
                }

                log.info("Ticket {} status changed from {} to {}", ticketId, oldStatus, request.getStatus());
            }

            if (request.getAssigneeId() != null) {
                UserEntity assignee = userRepo.findById(request.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assignee", "id", request.getAssigneeId()));
                ticket.setAssignee(assignee);
                log.info("Ticket {} assigned to user {}", ticketId, request.getAssigneeId());
            }

            if (request.getGroupId() != null) {
                EngineerGroupEntity group = groupRepo.findById(request.getGroupId())
                        .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
                ticket.setGroup(group);
                log.info("Ticket {} assigned to group {}", ticketId, request.getGroupId());
            }

            if (request.getDueAt() != null) {
                ticket.setDueAt(request.getDueAt());
            }
        }

        ticket = ticketRepo.save(ticket);
        log.info("Ticket {} updated successfully", ticketId);

        return TicketMapper.toResponse(ticket);
    }

    /**
     * Assign ticket to user
     * Engineers can self-assign, admins can assign to anyone
     * @param ticketId ticket ID
     * @param assigneeId assignee user ID
     * @return updated ticket DTO
     */
    @Transactional
    public TicketResponse assignTicket(Long ticketId, Long assigneeId) {
        log.info("Assigning ticket {} to user {}", ticketId, assigneeId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        // Check if ticket is already assigned
        if (ticket.getAssignee() != null) {
            // Only admin can reassign
            if (!SecurityUtils.hasRole("ADMIN")) {
                log.warn("User {} tried to reassign already assigned ticket {}", currentUserId, ticketId);
                throw new AccessDeniedException("Ticket is already assigned. Only admins can reassign.");
            }
        }

        UserEntity assignee = userRepo.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee", "id", assigneeId));

        // Verify assignee is engineer or admin
        if (assignee.getRole() != UserRole.ENGINEER && assignee.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Can only assign tickets to engineers or admins");
        }

        // Engineers can only self-assign, admins can assign to anyone
        if (SecurityUtils.hasRole("ENGINEER") && !assigneeId.equals(currentUserId)) {
            log.warn("Engineer {} tried to assign ticket {} to another user {}", currentUserId, ticketId, assigneeId);
            throw new AccessDeniedException("Engineers can only assign tickets to themselves");
        }

        ticket.setAssignee(assignee);

        // Auto-change status to IN_PROGRESS if it's NEW
        if (ticket.getStatus() == TicketStatus.NEW) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            log.info("Ticket {} status auto-changed to IN_PROGRESS", ticketId);
        }

        ticket = ticketRepo.save(ticket);
        log.info("Ticket {} assigned successfully to user {}", ticketId, assigneeId);

        // TODO: Send notification to requester about ticket assignment
        // This will be implemented in the notification module

        return TicketMapper.toResponse(ticket);
    }

    /**
     * Take ticket (self-assign) - for engineers
     * Engineer takes an unassigned ticket
     * @param ticketId ticket ID
     * @return updated ticket DTO
     */
    @Transactional
    public TicketResponse takeTicket(Long ticketId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        log.info("User {} is taking ticket {}", currentUserId, ticketId);

        // Only engineers and admins can take tickets
        if (!SecurityUtils.hasAnyRole("ADMIN", "ENGINEER")) {
            throw new AccessDeniedException("Only engineers and admins can take tickets");
        }

        return assignTicket(ticketId, currentUserId);
    }

    /**
     * Change ticket status (admin/engineer only)
     * @param ticketId ticket ID
     * @param newStatus new status
     * @return updated ticket DTO
     */
    @Transactional
    public TicketResponse changeTicketStatus(Long ticketId, TicketStatus newStatus) {
        log.info("Changing ticket {} status to {}", ticketId, newStatus);

        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);

        // Set closedAt when ticket is closed or resolved
        if ((newStatus == TicketStatus.CLOSED || newStatus == TicketStatus.RESOLVED) &&
            ticket.getClosedAt() == null) {
            ticket.setClosedAt(LocalDateTime.now());
            log.info("Ticket {} closed/resolved at {}", ticketId, ticket.getClosedAt());

            // Notify requester and assignee (email)
            if (ticket.getRequester() != null) {
                notificationService.notifyUserEmail(
                        ticket.getRequester().getId(),
                        "Your ticket status changed",
                        "Ticket #" + ticket.getId() + " status: " + newStatus
                );
            }
            if (ticket.getAssignee() != null) {
                notificationService.notifyUserEmail(
                        ticket.getAssignee().getId(),
                        "Assigned ticket status changed",
                        "Ticket #" + ticket.getId() + " status: " + newStatus
                );
            }
        }

        ticket = ticketRepo.save(ticket);
        log.info("Ticket {} status changed from {} to {}", ticketId, oldStatus, newStatus);

        return TicketMapper.toResponse(ticket);
    }

    /**
     * Delete ticket (admin only)
     * @param ticketId ticket ID
     */
    @Transactional
    public void deleteTicket(Long ticketId) {
        log.info("Deleting ticket ID: {}", ticketId);

        TicketEntity ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        ticketRepo.delete(ticket);
        log.info("Ticket {} deleted successfully", ticketId);
    }

    /**
     * Get ticket statistics (admin/agent only)
     * @return statistics map
     */
    @Transactional(readOnly = true)
    public TicketStatistics getTicketStatistics() {
        log.info("Fetching ticket statistics");

        return TicketStatistics.builder()
                .totalTickets(ticketRepo.count())
                .newTickets(ticketRepo.countByStatus(TicketStatus.NEW))
                .inProgressTickets(ticketRepo.countByStatus(TicketStatus.IN_PROGRESS))
                .resolvedTickets(ticketRepo.countByStatus(TicketStatus.RESOLVED))
                .closedTickets(ticketRepo.countByStatus(TicketStatus.CLOSED))
                .build();
    }

    /**
     * Ticket statistics DTO
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TicketStatistics {
        private long totalTickets;
        private long newTickets;
        private long inProgressTickets;
        private long resolvedTickets;
        private long closedTickets;
    }
}

