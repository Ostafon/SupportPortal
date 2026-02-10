package com.ostafon.supportportal.chat.service;

import com.ostafon.supportportal.chat.dto.request.MessageRequest;
import com.ostafon.supportportal.chat.dto.request.UpdateMessageRequest;
import com.ostafon.supportportal.chat.dto.response.ChatHistoryResponse;
import com.ostafon.supportportal.chat.dto.response.ChatResponse;
import com.ostafon.supportportal.chat.dto.response.MessageDeletedResponse;
import com.ostafon.supportportal.chat.dto.response.MessageResponse;
import com.ostafon.supportportal.chat.repo.TicketMessageRepo;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.notifications.service.NotificationService;
import com.ostafon.supportportal.tickets.model.TicketEntity;
import com.ostafon.supportportal.tickets.model.TicketMessageEntity;
import com.ostafon.supportportal.tickets.repo.TicketRepo;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final TicketRepo ticketRepo;
    private final TicketMessageRepo messageRepo;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    /**
     * Send message to ticket chat
     */
    @Transactional
    public MessageResponse sendMessage(MessageRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        TicketEntity ticket = ticketRepo.findByIdWithDetails(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", request.getTicketId()));

        if (!hasChatAccess(ticket, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to write in this ticket chat");
        }

        UserEntity author = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        TicketMessageEntity message = TicketMessageEntity.builder()
                .ticket(ticket)
                .author(author)
                .message(request.getMessage().trim())
                .build();

        message = messageRepo.save(message);

        log.info("Message sent to ticket {} by user {}", request.getTicketId(), currentUserId);

        MessageResponse response = toMessageResponse(message);
        messagingTemplate.convertAndSend("/topic/tickets/" + request.getTicketId(), response);

        notifyParticipants(ticket, author.getId(), message.getMessage());
        return response;
    }

    /**
     * Get full chat history for ticket
     */
    @Transactional(readOnly = true)
    public ChatHistoryResponse getChatHistory(Long ticketId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (!hasChatAccess(ticket, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to view this ticket chat");
        }

        List<MessageResponse> messages = messageRepo.findByTicket_IdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());

        return ChatHistoryResponse.builder()
                .ticketId(ticketId)
                .messages(messages)
                .build();
    }

    /**
     * Get chat summary for ticket
     */
    @Transactional(readOnly = true)
    public ChatResponse getChatSummary(Long ticketId) {
        TicketEntity ticket = ticketRepo.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        Long messagesCount = messageRepo.countByTicket_Id(ticketId);
        String lastMessage = messageRepo.findTop1ByTicket_IdOrderByCreatedAtDesc(ticketId)
                .map(TicketMessageEntity::getMessage)
                .orElse(null);

        return ChatResponse.builder()
                .ticketId(ticketId)
                .ticketTitle(ticket.getTitle())
                .messagesCount(messagesCount)
                .lastMessage(lastMessage)
                .build();
    }

    @Transactional
    public MessageResponse updateMessage(Long messageId, UpdateMessageRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        TicketMessageEntity message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!canEditOrDelete(message, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to edit this message");
        }

        message.setMessage(request.getMessage().trim());
        message = messageRepo.save(message);

        MessageResponse response = toMessageResponse(message);
        messagingTemplate.convertAndSend("/topic/tickets/" + message.getTicket().getId() + "/message-updated", response);
        return response;
    }

    @Transactional
    public MessageDeletedResponse deleteMessage(Long messageId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        TicketMessageEntity message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));

        if (!canEditOrDelete(message, currentUserId)) {
            throw new AccessDeniedException("You don't have permission to delete this message");
        }

        Long ticketId = message.getTicket().getId();
        messageRepo.delete(message);

        MessageDeletedResponse response = MessageDeletedResponse.builder()
                .messageId(messageId)
                .ticketId(ticketId)
                .build();

        messagingTemplate.convertAndSend("/topic/tickets/" + ticketId + "/message-deleted", response);
        return response;
    }

    private boolean hasChatAccess(TicketEntity ticket, Long currentUserId) {
        if (SecurityUtils.hasAnyRole("ADMIN", "ENGINEER")) {
            return true;
        }
        boolean isRequester = ticket.getRequester() != null && ticket.getRequester().getId().equals(currentUserId);
        boolean isAssignee = ticket.getAssignee() != null && ticket.getAssignee().getId().equals(currentUserId);
        return isRequester || isAssignee;
    }

    private boolean canEditOrDelete(TicketMessageEntity message, Long currentUserId) {
        if (SecurityUtils.hasAnyRole("ADMIN", "ENGINEER")) {
            return true;
        }
        return message.getAuthor() != null && message.getAuthor().getId().equals(currentUserId);
    }

    private MessageResponse toMessageResponse(TicketMessageEntity message) {
        return MessageResponse.builder()
                .id(message.getId())
                .ticketId(message.getTicket().getId())
                .message(message.getMessage())
                .authorId(message.getAuthor().getId())
                .authorName(message.getAuthor().getFirstName() + " " + message.getAuthor().getLastName())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private void notifyParticipants(TicketEntity ticket, Long authorId, String messageText) {
        if (ticket.getRequester() != null && !ticket.getRequester().getId().equals(authorId)) {
            notificationService.notifyUserInApp(
                    ticket.getRequester().getId(),
                    "New message in ticket #" + ticket.getId(),
                    messageText
            );
        }
        if (ticket.getAssignee() != null && !ticket.getAssignee().getId().equals(authorId)) {
            notificationService.notifyUserInApp(
                    ticket.getAssignee().getId(),
                    "New message in ticket #" + ticket.getId(),
                    messageText
            );
        }
    }
}
