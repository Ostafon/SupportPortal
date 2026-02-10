package com.ostafon.supportportal.tickets.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateTicketRequest {

    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @JsonProperty("priority")
    @NotNull(message = "Priority is required")
    private TicketPriority priority;


    @JsonProperty("assigneeId")
    private Long assigneeId;

    @JsonProperty("groupId")
    private Long groupId;

    @JsonProperty("dueAt")
    private LocalDateTime dueAt;
}