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
@Table(name = "`users`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "username", unique = true, nullable = false, length = 50)
	private String username;

	@Column(name = "email", unique = true, nullable = false, length = 100)
	private String email;

	@Column(name = "verified", nullable = false)
	@Builder.Default
	private boolean verified = false;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "calendar_notify", nullable = false)
	@Builder.Default
	private boolean calendarNotify = true;

	@Column(name = "invite_notify", nullable = false)
	@Builder.Default
	private boolean inviteNotify = true;
}
