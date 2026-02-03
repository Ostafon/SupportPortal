package com.ostafon.supportportal.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MessageRequest {

    @JsonProperty("ticketId")
    private Long ticketId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("authorId")
    private Long authorId;
}