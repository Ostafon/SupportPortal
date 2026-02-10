package com.ostafon.supportportal.notifications.service;

import com.ostafon.supportportal.common.enums.NotificationChannel;
import com.ostafon.supportportal.common.enums.NotificationStatus;
import com.ostafon.supportportal.common.exception.ResourceNotFoundException;
import com.ostafon.supportportal.common.utils.SecurityUtils;
import com.ostafon.supportportal.notifications.dto.request.CreateNotificationRequest;
import com.ostafon.supportportal.notifications.dto.response.NotificationResponse;
import com.ostafon.supportportal.notifications.mapper.NotificationMapper;
import com.ostafon.supportportal.notifications.model.NotificationEntity;
import com.ostafon.supportportal.notifications.repo.NotificationRepo;
import com.ostafon.supportportal.notifications.sender.EmailSender;
import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailSender emailSender;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new ResourceNotFoundException("User", "id", null);
        }

        return notificationRepo.findByUser_IdOrderByCreatedAtDesc(currentUserId, pageable)
                .map(NotificationMapper::toResponse);
    }

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        UserEntity user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        NotificationEntity entity = NotificationEntity.builder()
                .user(user)
                .channel(request.getChannel())
                .title(request.getTitle())
                .body(request.getBody())
                .status(NotificationStatus.PENDING)
                .build();

        entity = notificationRepo.save(entity);
        NotificationResponse response = NotificationMapper.toResponse(entity);

        if (request.getChannel() == NotificationChannel.IN_APP) {
            entity.setStatus(NotificationStatus.SENT);
            entity.setSentAt(LocalDateTime.now());
            notificationRepo.save(entity);
            messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), response);
        }

        if (request.getChannel() == NotificationChannel.EMAIL) {
            sendEmailAndUpdateStatus(entity, user.getEmail());
        }

        return NotificationMapper.toResponse(entity);
    }

    @Transactional
    public void notifyUserInApp(Long userId, String title, String body) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        NotificationEntity entity = NotificationEntity.builder()
                .user(user)
                .channel(NotificationChannel.IN_APP)
                .title(title)
                .body(body)
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        entity = notificationRepo.save(entity);
        NotificationResponse response = NotificationMapper.toResponse(entity);
        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), response);
    }

    @Transactional
    public void notifyUsersInApp(List<UserEntity> users, String title, String body) {
        for (UserEntity user : users) {
            NotificationEntity entity = NotificationEntity.builder()
                    .user(user)
                    .channel(NotificationChannel.IN_APP)
                    .title(title)
                    .body(body)
                    .status(NotificationStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();
            entity = notificationRepo.save(entity);
            NotificationResponse response = NotificationMapper.toResponse(entity);
            messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), response);
        }
    }

    @Transactional
    public void notifyUserEmail(Long userId, String title, String body) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        NotificationEntity entity = NotificationEntity.builder()
                .user(user)
                .channel(NotificationChannel.EMAIL)
                .title(title)
                .body(body)
                .status(NotificationStatus.PENDING)
                .build();

        entity = notificationRepo.save(entity);
        sendEmailAndUpdateStatus(entity, user.getEmail());
    }

    @Transactional
    public void notifyUsersEmail(List<UserEntity> users, String title, String body) {
        for (UserEntity user : users) {
            NotificationEntity entity = NotificationEntity.builder()
                    .user(user)
                    .channel(NotificationChannel.EMAIL)
                    .title(title)
                    .body(body)
                    .status(NotificationStatus.PENDING)
                    .build();
            entity = notificationRepo.save(entity);
            sendEmailAndUpdateStatus(entity, user.getEmail());
        }
    }

    private void sendEmailAndUpdateStatus(NotificationEntity entity, String email) {
        try {
            emailSender.send(email, entity.getTitle(), entity.getBody());
            entity.setStatus(NotificationStatus.SENT);
            entity.setSentAt(LocalDateTime.now());
        } catch (Exception ex) {
            log.warn("Email notification failed for {}: {}", email, ex.getMessage());
            entity.setStatus(NotificationStatus.FAILED);
        }
        notificationRepo.save(entity);
    }
}
