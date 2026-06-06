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
@Table(name = "user_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "user_id")
	private UUID userId;

	@Column(name = "group_id")
	private UUID groupId;

	@Column(name = "role", nullable = false, length = 50)
	@Builder.Default
	private String role = "MEMBER";

	@Column(name = "notify", nullable = false)
	@Builder.Default
	private boolean notify = true;
}
