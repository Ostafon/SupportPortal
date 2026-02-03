package com.ostafon.supportportal.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MessageResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ticketId")
    private Long ticketId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("authorName")
    private String authorName;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}