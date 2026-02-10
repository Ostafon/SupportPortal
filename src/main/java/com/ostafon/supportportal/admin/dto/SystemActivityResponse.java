package com.ostafon.supportportal.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * System activity log entry DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemActivityResponse {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("activityType")
    private String activityType; // USER_REGISTERED, TICKET_CREATED, TICKET_ASSIGNED, etc.

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("details")
    private String details;
}

