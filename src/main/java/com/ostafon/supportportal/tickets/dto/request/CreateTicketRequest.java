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
    private Long assigneeId;  // ID инженера, которому будет назначен тикет (если есть)

    @JsonProperty("groupId")
    private Long groupId;  // ID группы инженеров (если необходимо)

    @JsonProperty("dueAt")
    private LocalDateTime dueAt;  // Срок выполнения
}