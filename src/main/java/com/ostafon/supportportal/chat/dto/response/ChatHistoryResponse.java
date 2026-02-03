package com.ostafon.supportportal.chat.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChatHistoryResponse {

    private Long ticketId;

    private List<MessageResponse> messages;
}