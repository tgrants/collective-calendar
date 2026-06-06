package com.collectivecalendar.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invite {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "group_id", nullable = false)
	private UUID groupId;

	@Column(name = "status", nullable = false, length = 50)
	@Builder.Default
	private String status = "PENDING";

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private ZonedDateTime createdAt;
}
