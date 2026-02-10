package com.ostafon.supportportal.chat.websoket;

import com.ostafon.supportportal.chat.dto.request.MessageRequest;
import com.ostafon.supportportal.chat.dto.request.UpdateMessageRequest;
import com.ostafon.supportportal.chat.dto.response.MessageDeletedResponse;
import com.ostafon.supportportal.chat.dto.response.MessageResponse;
import com.ostafon.supportportal.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for real-time chat
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/tickets/{ticketId}/send")
    public MessageResponse sendMessage(@DestinationVariable Long ticketId, @Payload MessageRequest request) {
        request.setTicketId(ticketId);
        log.info("WS: send message to ticket {}", ticketId);
        return chatService.sendMessage(request);
    }

    @MessageMapping("/tickets/{ticketId}/edit/{messageId}")
    public MessageResponse editMessage(
            @DestinationVariable Long ticketId,
            @DestinationVariable Long messageId,
            @Payload UpdateMessageRequest request) {
        log.info("WS: edit message {} for ticket {}", messageId, ticketId);
        return chatService.updateMessage(messageId, request);
    }

    @MessageMapping("/tickets/{ticketId}/delete/{messageId}")
    public MessageDeletedResponse deleteMessage(
            @DestinationVariable Long ticketId,
            @DestinationVariable Long messageId) {
        log.info("WS: delete message {} for ticket {}", messageId, ticketId);
        return chatService.deleteMessage(messageId);
    }
}

