package com.ostafon.supportportal.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDeletedResponse {

    @JsonProperty("messageId")
    private Long messageId;

    @JsonProperty("ticketId")
    private Long ticketId;
}

