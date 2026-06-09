package com.collectivecalendar.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.NotificationStatus;
import com.collectivecalendar.model.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
	List<Notification> findByStatusAndTypeAndRetriesLessThan(
		NotificationStatus status, 
		NotificationType type, 
		int maxRetries
	);

	List<Notification> findByNotifyUserUid(UUID userUid);

	List<Notification> findByNotifyUserUidAndSeenFalse(UUID userUid);
}
