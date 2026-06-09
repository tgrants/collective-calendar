package com.collectivecalendar.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.collectivecalendar.event.service.EventService;
import com.collectivecalendar.model.Event;
import com.collectivecalendar.model.GroupEvent;
import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.NotificationStatus;
import com.collectivecalendar.model.NotificationType;
import com.collectivecalendar.model.Notify;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.EventRepository;
import com.collectivecalendar.repository.GroupEventRepository;
import com.collectivecalendar.repository.NotificationRepository;
import com.collectivecalendar.repository.NotifyRepository;
import com.collectivecalendar.repository.UserGroupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationGeneratorService {

	private final EventRepository eventRepository;
	private final EventService eventService;
	private final GroupEventRepository groupEventRepository;
	private final UserGroupRepository userGroupRepository;
	private final NotificationRepository notificationRepository;
	private final NotifyRepository notifyRepository;

	private static final int PRE_GENERATE_COUNT = 3;

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void onStartupCatchUp() {
		log.info("System startup detected! Executing notification catch-up and pre-generation cycle.");
		preGenerateFutureNotificationsLogic();
	}

	@Scheduled(cron = "0 0 */1 * * *")
	@Transactional
	public void scheduledPreGeneration() {
		log.info("Executing scheduled hourly notification pre-generation cycle.");
		preGenerateFutureNotificationsLogic();
	}

	private void preGenerateFutureNotificationsLogic() {
		List<Event> allEvents = eventRepository.findAll();
		LocalDateTime now = LocalDateTime.now();

		for (Event event : allEvents) {
			try {
				List<LocalDateTime> futureInstances = eventService.getInstances(event, PRE_GENERATE_COUNT);

				if (futureInstances == null || futureInstances.isEmpty()) {
					continue;
				}

				stageInstancesForEvent(event, futureInstances, now);

			} catch (Exception e) {
				log.error("Failed to pre-generate notifications for event UID: " + event.getUid(), e);
			}
		}
		log.info("Notification pre-generation cycle completed successfully.");
	}

	private void stageInstancesForEvent(Event event, List<LocalDateTime> instances, LocalDateTime now) {
		List<GroupEvent> groupEvents = groupEventRepository.findByEventId(event.getUid());

		LocalDateTime gracePeriodLimit = now.minusMinutes(5);

		for (GroupEvent groupEvent : groupEvents) {
			List<UserGroup> activeSubscribers = userGroupRepository.findByGroupIdAndNotifyTrue(groupEvent.getGroupId());

			for (UserGroup subscriber : activeSubscribers) {
				UUID userId = subscriber.getUserId();
				Notify overrideConfig = notifyRepository.findByUserUidAndEventUid(userId, event.getUid()).orElse(null);
				UUID notifyUid = (overrideConfig != null) ? overrideConfig.getUid() : null;

				for (LocalDateTime instanceTime : instances) {
					if (instanceTime.isBefore(gracePeriodLimit)) {
					continue; 
				}

					if (overrideConfig == null || overrideConfig.isNotifyEmail()) {
						boolean exists = notificationRepository.existsByNotifyUserUidAndNotifyEventUidAndScheduledForAndType(
								userId, event.getUid(), instanceTime, NotificationType.EMAIL
						);

						if (!exists) {
							Notification emailNotif = Notification.builder()
									.notifyUserUid(userId)
									.notifyEventUid(event.getUid())
									.type(NotificationType.EMAIL)
									.status(NotificationStatus.PENDING)
									.notifyUid(notifyUid)
									.scheduledFor(instanceTime)
									.build();
							notificationRepository.save(emailNotif);
						}
					}

					if (overrideConfig == null || overrideConfig.isNotifyInapp()) {
						NotificationType reminderType = NotificationType.IN_APP;
						boolean exists = notificationRepository.existsByNotifyUserUidAndNotifyEventUidAndScheduledForAndType(
								userId, event.getUid(), instanceTime, reminderType
						);

						if (!exists) {
							Notification inAppNotif = Notification.builder()
									.notifyUserUid(userId)
									.notifyEventUid(event.getUid())
									.type(reminderType)
									.status(NotificationStatus.PENDING)
									.notifyUid(notifyUid)
									.scheduledFor(instanceTime)
									.build();
							notificationRepository.save(inAppNotif);
						}
					}
				}
			}
		}
	}
}
