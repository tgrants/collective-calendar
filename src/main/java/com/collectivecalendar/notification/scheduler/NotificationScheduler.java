package com.collectivecalendar.notification.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.NotificationStatus;
import com.collectivecalendar.model.NotificationType;
import com.collectivecalendar.notification.service.EmailSenderService;
import com.collectivecalendar.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

	private final NotificationRepository notificationRepository;
	private final EmailSenderService emailSenderService;
	// TODO: Inject NotifyRepository when created to fetch notification-specific overrides
	// private final NotifyRepository notifyRepository;

	private static final int MAX_RETRY_LIMIT = 3;

	@Scheduled(cron = "0 */1 * * * *") // Every minute
	@Transactional
	public void processPendingEmailQueue() {
		List<Notification> pendingEmails = notificationRepository.findByStatusAndTypeAndRetriesLessThan(
				NotificationStatus.PENDING, 
				NotificationType.EMAIL, 
				MAX_RETRY_LIMIT
		);

		if (pendingEmails.isEmpty()) {
			return;
		}

		log.info("Found {} pending email notifications to process.", pendingEmails.size());

		for (Notification notification : pendingEmails) {
			try {
				// Check if notify -> notify_email is TRUE for this specific notification
				boolean isEmailNotificationAllowed = true;
				
				if (notification.getNotifyUid() != null) {
					// TODO: Fetch from notify table using notification.getNotifyUid()
					// NotifyConfig notifyConfig = notifyRepository.findById(notification.getNotifyUid()).orElse(null);
					// if (notifyConfig != null) { isEmailNotificationAllowed = notifyConfig.isNotifyEmail(); }
				}

				if (!isEmailNotificationAllowed) {
					log.info("Skipping notification {} because notify_email is disabled for this configuration row.", notification.getUid());
					notification.setStatus(NotificationStatus.FAILED); // Fail it so it doesn't loop forever
					continue;
				}

				// TODO: Fetch user entity data via notification.getNotifyUserUid()
				String recipientEmail = "user-email-placeholder@example.local"; 
				String username = "User";

				// TODO: Fetch event entity data via notification.getNotifyEventUid()
				String eventName = "Upcoming Calendar Event";

				String subject = "Calendar Alert: " + eventName;
				String htmlBody = String.format(
						"<h2>Hello %s,</h2><p>You have an upcoming event: <strong>%s</strong>.</p>", 
						username, eventName
				);

				boolean isSuccess = emailSenderService.sendHtmlEmail(recipientEmail, subject, htmlBody);

				if (isSuccess) {
					notification.setStatus(NotificationStatus.SENT);
				} else {
					handleFailure(notification);
				}

			} catch (Exception e) {
				log.error("Unexpected error processing notification token " + notification.getUid(), e);
				handleFailure(notification);
			}
		}

		notificationRepository.saveAll(pendingEmails);
	}

	private void handleFailure(Notification notification) {
		int nextRetryCount = notification.getRetries() + 1;
		notification.setRetries(nextRetryCount);

		if (nextRetryCount >= MAX_RETRY_LIMIT) {
			notification.setStatus(NotificationStatus.FAILED);
			log.error("Notification {} has exceeded max retry limits. Terminating alerts.", notification.getUid());
		} else {
			log.warn("Notification {} delivery failed. Queue retry count set to: {}", notification.getUid(), nextRetryCount);
		}
	}
}
