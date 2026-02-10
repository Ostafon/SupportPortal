package com.ostafon.supportportal.notifications.repo;

import com.ostafon.supportportal.notifications.model.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

