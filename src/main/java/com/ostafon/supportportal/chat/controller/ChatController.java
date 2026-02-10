package com.ostafon.supportportal.chat.controller;

import com.ostafon.supportportal.chat.dto.request.MessageRequest;
import com.ostafon.supportportal.chat.dto.request.UpdateMessageRequest;
import com.ostafon.supportportal.chat.dto.response.ChatHistoryResponse;
import com.ostafon.supportportal.chat.dto.response.ChatResponse;
import com.ostafon.supportportal.chat.dto.response.MessageDeletedResponse;
import com.ostafon.supportportal.chat.dto.response.MessageResponse;
import com.ostafon.supportportal.chat.service.ChatService;
import com.ostafon.supportportal.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for ticket chat
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "Ticket chat messages")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'USER')")
public class ChatController {

    private final ChatService chatService;

    /**
     * Send message to ticket chat
     */
    @PostMapping("/messages")
    @Operation(
            summary = "Send message",
            description = "Send a message to a ticket chat"
    )
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageRequest request) {

        log.info("REST: Send message to ticket {}", request.getTicketId());

        MessageResponse message = chatService.sendMessage(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent", message));
    }

    /**
     * Get ticket chat history
     */
    @GetMapping("/tickets/{ticketId}/history")
    @Operation(
            summary = "Get chat history",
            description = "Get full message history for a ticket"
    )
    public ResponseEntity<ApiResponse<ChatHistoryResponse>> getChatHistory(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId) {

        log.info("REST: Get chat history for ticket {}", ticketId);

        ChatHistoryResponse history = chatService.getChatHistory(ticketId);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * Get chat summary
     */
    @GetMapping("/tickets/{ticketId}/summary")
    @Operation(
            summary = "Get chat summary",
            description = "Get ticket chat summary with message count and last message"
    )
    public ResponseEntity<ApiResponse<ChatResponse>> getChatSummary(
            @PathVariable @Parameter(description = "Ticket ID") Long ticketId) {

        log.info("REST: Get chat summary for ticket {}", ticketId);

        ChatResponse summary = chatService.getChatSummary(ticketId);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * Update a chat message
     */
    @PutMapping("/messages/{messageId}")
    @Operation(
            summary = "Update message",
            description = "Update a chat message"
    )
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable @Parameter(description = "Message ID") Long messageId,
            @Valid @RequestBody UpdateMessageRequest request) {

        log.info("REST: Update message {}", messageId);

        MessageResponse message = chatService.updateMessage(messageId, request);

        return ResponseEntity.ok(ApiResponse.success("Message updated", message));
    }

    /**
     * Delete a chat message
     */
    @DeleteMapping("/messages/{messageId}")
    @Operation(
            summary = "Delete message",
            description = "Delete a chat message"
    )
    public ResponseEntity<ApiResponse<MessageDeletedResponse>> deleteMessage(
            @PathVariable @Parameter(description = "Message ID") Long messageId) {

        log.info("REST: Delete message {}", messageId);

        MessageDeletedResponse deleted = chatService.deleteMessage(messageId);

        return ResponseEntity.ok(ApiResponse.success("Message deleted", deleted));
    }
}
