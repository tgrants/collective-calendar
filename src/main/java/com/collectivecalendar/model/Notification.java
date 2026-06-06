package com.collectivecalendar.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
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

	@Column(name = "type", nullable = false, length = 50)
	private String type;

	@Column(name = "status", nullable = false, length = 50)
	private String status;

	@Column(name = "retries", nullable = false)
	@Builder.Default
	private int retries = 0;

	@Column(name = "seen", nullable = false)
	@Builder.Default
	private boolean seen = false;

	@Column(name = "notify_uid")
	private UUID notifyUid;
}
