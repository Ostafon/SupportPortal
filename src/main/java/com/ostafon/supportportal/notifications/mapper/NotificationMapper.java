package com.ostafon.supportportal.notifications.mapper;

import com.ostafon.supportportal.notifications.dto.response.NotificationResponse;
import com.ostafon.supportportal.notifications.model.NotificationEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMapper {

    public static NotificationResponse toResponse(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .channel(entity.getChannel())
                .title(entity.getTitle())
                .body(entity.getBody())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .sentAt(entity.getSentAt())
                .build();
    }
}

