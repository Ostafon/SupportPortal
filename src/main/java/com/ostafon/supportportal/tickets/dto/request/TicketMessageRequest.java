package com.ostafon.supportportal.tickets.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TicketMessageRequest {

    @JsonProperty("ticketId")
    private Long ticketId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("authorId")
    private Long authorId;
}