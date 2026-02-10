package com.ostafon.supportportal.tickets.mapper;

import com.ostafon.supportportal.tickets.dto.response.TicketResponse;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import lombok.experimental.UtilityClass;

/**
 * Mapper for Ticket entities and DTOs
 */
@UtilityClass
public class TicketMapper {

    /**
     * Convert TicketEntity to TicketResponse DTO
     * @param ticket ticket entity
     * @return ticket response DTO
     */
    public static TicketResponse toResponse(TicketEntity ticket) {
        if (ticket == null) {
            return null;
        }

        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .requesterId(ticket.getRequester() != null ? ticket.getRequester().getId() : null)
                .requesterName(ticket.getRequester() != null
                        ? ticket.getRequester().getFirstName() + " " + ticket.getRequester().getLastName()
                        : null)
                .requesterEmail(ticket.getRequester() != null ? ticket.getRequester().getEmail() : null)
                .assigneeId(ticket.getAssignee() != null ? ticket.getAssignee().getId() : null)
                .assigneeName(ticket.getAssignee() != null
                        ? ticket.getAssignee().getFirstName() + " " + ticket.getAssignee().getLastName()
                        : null)
                .groupId(ticket.getGroup() != null ? ticket.getGroup().getId() : null)
                .groupName(ticket.getGroup() != null ? ticket.getGroup().getName() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .dueAt(ticket.getDueAt())
                .closedAt(ticket.getClosedAt())
                .build();
    }
}

