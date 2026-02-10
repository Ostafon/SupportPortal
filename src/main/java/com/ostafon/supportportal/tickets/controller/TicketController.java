package com.ostafon.supportportal.tickets.controller;

import com.ostafon.supportportal.common.dto.ApiResponse;
import com.ostafon.supportportal.common.enums.TicketStatus;
import com.ostafon.supportportal.tickets.dto.request.CreateTicketRequest;
import com.ostafon.supportportal.tickets.dto.request.UpdateTicketRequest;
import com.ostafon.supportportal.tickets.dto.response.TicketResponse;
import com.ostafon.supportportal.tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for ticket management
 * Handles ticket CRUD operations, assignment, and status management
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tickets", description = "Support ticket management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TicketController {

    private final TicketService ticketService;

    /**
     * Create a new support ticket
     * Available to all authenticated users
     */
    @PostMapping
    @Operation(
            summary = "Create new ticket",
            description = "Create a new support ticket. All tickets are created unassigned. " +
                         "Engineers will be notified and can take the ticket. " +
                         "Only admins can assign tickets on creation."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody CreateTicketRequest request) {

        log.info("REST: Create ticket request: {}", request.getTitle());

        TicketResponse ticket = ticketService.createTicket(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket created successfully", ticket));
    }

    /**
     * Get ticket by ID
     * Users can view their own tickets, agents/admins can view all
     */
    @GetMapping("/{ticketId}")
    @Operation(
            summary = "Get ticket by ID",
            description = "Retrieve ticket details. Users can only view their own tickets. " +
                         "Agents and admins can view any ticket."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId) {

        log.info("REST: Get ticket by ID: {}", ticketId);

        TicketResponse ticket = ticketService.getTicketById(ticketId);

        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    /**
     * Get all tickets with pagination
     * Users see their own tickets, agents/admins see all
     */
    @GetMapping
    @Operation(
            summary = "Get all tickets",
            description = "Retrieve all tickets with pagination. Users see only their own tickets. " +
                         "Agents and admins see all tickets."
    )
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getAllTickets(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-based)") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "createdAt") @Parameter(description = "Sort by field") String sortBy,
            @RequestParam(defaultValue = "DESC") @Parameter(description = "Sort direction") Sort.Direction direction) {

        log.info("REST: Get all tickets - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<TicketResponse> tickets = ticketService.getAllTickets(pageable);

        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Get tickets by status (admin/agent only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Get tickets by status",
            description = "Retrieve tickets filtered by status. Admin and agent only."
    )
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getTicketsByStatus(
            @PathVariable @Parameter(description = "Ticket status") TicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get tickets by status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponse> tickets = ticketService.getTicketsByStatus(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Get tickets assigned to current user
     */
    @GetMapping("/my-assigned")
    @Operation(
            summary = "Get my assigned tickets",
            description = "Retrieve tickets assigned to the current user"
    )
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getMyAssignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get my assigned tickets");

        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponse> tickets = ticketService.getMyAssignedTickets(pageable);

        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Get unassigned tickets (admin/agent only)
     */
    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Get unassigned tickets",
            description = "Retrieve tickets that are not assigned to anyone. Admin and agent only."
    )
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getUnassignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("REST: Get unassigned tickets");

        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponse> tickets = ticketService.getUnassignedTickets(pageable);

        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Update ticket
     * Users can update their own tickets (limited fields)
     * Agents/admins can update any ticket
     */
    @PutMapping("/{ticketId}")
    @Operation(
            summary = "Update ticket",
            description = "Update ticket details. Users can update only title and description of their own tickets. " +
                         "Agents and admins can update all fields of any ticket."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId,
            @Valid @RequestBody UpdateTicketRequest request) {

        log.info("REST: Update ticket ID: {}", ticketId);

        TicketResponse ticket = ticketService.updateTicket(ticketId, request);

        return ResponseEntity.ok(ApiResponse.success("Ticket updated successfully", ticket));
    }

    /**
     * Assign ticket to user (admin/agent only)
     */
    @PutMapping("/{ticketId}/assign/{assigneeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Assign ticket",
            description = "Assign ticket to a specific user. Admin and agent only."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId,
            @PathVariable @Parameter(description = "Assignee user ID") Long assigneeId) {

        log.info("REST: Assign ticket {} to user {}", ticketId, assigneeId);

        TicketResponse ticket = ticketService.assignTicket(ticketId, assigneeId);

        return ResponseEntity.ok(ApiResponse.success("Ticket assigned successfully", ticket));
    }

    /**
     * Take ticket (self-assign) - for engineers
     */
    @PutMapping("/{ticketId}/take")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(
            summary = "Take ticket",
            description = "Engineer takes an unassigned ticket (self-assign). " +
                         "Ticket status automatically changes to IN_PROGRESS."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> takeTicket(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId) {

        log.info("REST: Take ticket {}", ticketId);

        TicketResponse ticket = ticketService.takeTicket(ticketId);

        return ResponseEntity.ok(ApiResponse.success("Ticket taken successfully", ticket));
    }

    /**
     * Change ticket status (admin/engineer only)
     */
    @PutMapping("/{ticketId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(
            summary = "Change ticket status",
            description = "Change ticket status. Admin and agent only."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> changeTicketStatus(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId,
            @PathVariable @Parameter(description = "New status") TicketStatus status) {

        log.info("REST: Change ticket {} status to {}", ticketId, status);

        TicketResponse ticket = ticketService.changeTicketStatus(ticketId, status);

        return ResponseEntity.ok(ApiResponse.success("Ticket status changed successfully", ticket));
    }

    /**
     * Delete ticket (admin only)
     */
    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete ticket",
            description = "Delete ticket permanently. Admin only."
    )
    public ResponseEntity<ApiResponse<Void>> deleteTicket(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId) {

        log.info("REST: Delete ticket ID: {}", ticketId);

        ticketService.deleteTicket(ticketId);

        return ResponseEntity.ok(ApiResponse.success("Ticket deleted successfully", null));
    }

    /**
     * Get ticket statistics (admin/engineer only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER')")
    @Operation(
            summary = "Get ticket statistics",
            description = "Retrieve ticket statistics (counts by status). Admin and engineer only."
    )
    public ResponseEntity<ApiResponse<TicketService.TicketStatistics>> getTicketStatistics() {

        log.info("REST: Get ticket statistics");

        TicketService.TicketStatistics stats = ticketService.getTicketStatistics();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

