package com.ostafon.supportportal.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ostafon.supportportal.common.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {

    @JsonProperty("userId")
    @NotNull(message = "userId is required")
    private Long userId;

    @JsonProperty("channel")
    @NotNull(message = "channel is required")
    private NotificationChannel channel;

    @JsonProperty("title")
    @NotBlank(message = "title is required")
    private String title;

    @JsonProperty("body")
    @NotBlank(message = "body is required")
    private String body;
}

