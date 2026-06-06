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
@Table(name = "notify")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notify {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "user_uid")
	private UUID userUid;

	@Column(name = "event_uid")
	private UUID eventUid;

	@Column(name = "notify_email", nullable = false)
	@Builder.Default
	private boolean notifyEmail = true;

	@Column(name = "notify_inapp", nullable = false)
	@Builder.Default
	private boolean notifyInapp = true;
}
