package com.collectivecalendar.notification.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.collectivecalendar.model.Event;
import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.NotificationStatus;
import com.collectivecalendar.model.NotificationType;
import com.collectivecalendar.model.Notify;
import com.collectivecalendar.model.User;
import com.collectivecalendar.notification.service.EmailSenderService;
import com.collectivecalendar.repository.EventRepository;
import com.collectivecalendar.repository.NotificationRepository;
import com.collectivecalendar.repository.NotifyRepository;
import com.collectivecalendar.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

	private final NotificationRepository notificationRepository;
	private final EmailSenderService emailSenderService;
	private final NotifyRepository notifyRepository;
	private final UserRepository userRepository;
	private final EventRepository eventRepository;

	private static final int MAX_RETRY_LIMIT = 3;

	@Scheduled(cron = "0 */1 * * * *") // Runs every minute
	@Transactional
	public void processPendingEmailQueue() {
		List<Notification> pendingEmails = notificationRepository.findByStatusAndTypeAndRetriesLessThanAndScheduledForLessThanEqual(
			NotificationStatus.PENDING, 
			NotificationType.EMAIL, 
			MAX_RETRY_LIMIT,
			LocalDateTime.now(ZoneOffset.ofHours(3))
		);

		if (pendingEmails.isEmpty()) {
			return;
		}

		log.info("Found {} pending email notifications to process.", pendingEmails.size());

		for (Notification notification : pendingEmails) {
			try {
				UUID userId = notification.getNotifyUserUid();
				User user = userRepository.findById(userId).orElse(null);
				
				if (user == null) {
					log.error("Skipping notification {}: Target user {} does not exist.", notification.getUid(), userId);
					notification.setStatus(NotificationStatus.FAILED);
					continue;
				}

				if (!user.isVerified()) {
					log.info("Skipping notification {}: User's email ({}) is not verified.", notification.getUid(), user.getEmail());
					notification.setStatus(NotificationStatus.FAILED);
					continue;
				}

				if (!user.isCalendarNotify()) {
					log.info("Skipping notification {}: User {} has disabled calendar notifications.", notification.getUid(), user.getUsername());
					notification.setStatus(NotificationStatus.FAILED);
					continue;
				}

				boolean isEmailNotificationAllowed = true;
				if (notification.getNotifyUid() != null) {
					Notify notifyConfig = notifyRepository.findById(notification.getNotifyUid()).orElse(null);
					if (notifyConfig != null) {
						isEmailNotificationAllowed = notifyConfig.isNotifyEmail();
					}
				}

				if (!isEmailNotificationAllowed) {
					log.info("Skipping notification {}: notify_email is disabled for this specific event configuration.", notification.getUid());
					notification.setStatus(NotificationStatus.FAILED);
					continue;
				}

				UUID eventId = notification.getNotifyEventUid();
				Event event = eventRepository.findById(eventId).orElse(null);
				
				if (event == null) {
					log.error("Skipping notification {}: Reference event {} does not exist.", notification.getUid(), eventId);
					notification.setStatus(NotificationStatus.FAILED);
					continue;
				}

				String recipientEmail = user.getEmail();
				String username = user.getUsername();
				String eventName = event.getName();

				String subject = "Calendar Alert: " + eventName;
				String htmlBody = String.format(
					"<h2>Hello %s,</h2><p>You have an upcoming event: <strong>%s</strong>.</p>" +
					"<p>Start Time: %s</p>", 
					username, eventName, event.getStartTime()
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
