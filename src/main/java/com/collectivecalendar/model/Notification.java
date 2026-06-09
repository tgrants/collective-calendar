package com.collectivecalendar.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "notify_user_uid", nullable = false)
	private UUID notifyUserUid;

	@Column(name = "notify_event_uid", nullable = false)
	private UUID notifyEventUid;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 50)
	private NotificationType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 50)
	private NotificationStatus status;

	@Column(name = "retries", nullable = false)
	@Builder.Default
	private int retries = 0;

	@Column(name = "seen", nullable = false)
	@Builder.Default
	private boolean seen = false;

	@Column(name = "notify_uid")
	private UUID notifyUid;

	@Column(name = "scheduled_for", nullable = false)
	private LocalDateTime scheduledFor;
}
