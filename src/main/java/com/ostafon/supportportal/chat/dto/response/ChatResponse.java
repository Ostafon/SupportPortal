package com.ostafon.supportportal.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChatResponse {

    @JsonProperty("ticketId")
    private Long ticketId;

    @JsonProperty("ticketTitle")
    private String ticketTitle;

    @JsonProperty("messagesCount")
    private Long messagesCount;

    @JsonProperty("lastMessage")
    private String lastMessage;
}