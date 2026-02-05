package com.ostafon.supportportal.tickets.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.TicketPriority;
import com.ostafon.supportportal.common.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateTicketRequest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("priority")
    private TicketPriority priority;

    @JsonProperty("status")
    private TicketStatus status = TicketStatus.NEW;

    @JsonProperty("assigneeId")
    private Long assigneeId;

    @JsonProperty("groupId")
    private Long groupId;

    @JsonProperty("dueAt")
    private LocalDateTime dueAt;
}