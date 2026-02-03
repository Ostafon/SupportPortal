package com.ostafon.supportportal.tickets.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.TicketPriority;
import com.ostafon.supportportal.common.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TicketResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("priority")
    private TicketPriority priority;

    @JsonProperty("status")
    private TicketStatus status;

    @JsonProperty("assigneeId")
    private Long assigneeId;

    @JsonProperty("groupId")
    private Long groupId;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("dueAt")
    private LocalDateTime dueAt;

    @JsonProperty("closedAt")
    private LocalDateTime closedAt;
}