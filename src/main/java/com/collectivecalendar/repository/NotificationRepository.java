package com.collectivecalendar.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.NotificationStatus;
import com.collectivecalendar.model.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
	List<Notification> findByStatusAndTypeAndRetriesLessThanAndScheduledForLessThanEqual(
		NotificationStatus status, 
		NotificationType type, 
		int maxRetries, 
		LocalDateTime dateTime
	);

	List<Notification> findByNotifyUserUid(UUID userUid);

	List<Notification> findByNotifyUserUidAndSeenFalse(UUID userUid);

	boolean existsByNotifyUserUidAndNotifyEventUidAndScheduledForAndType(
		UUID userUid, UUID eventUid, LocalDateTime scheduledFor, NotificationType type);
}
