package com.collectivecalendar.model;

import java.time.ZonedDateTime;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "uid", updatable = false, nullable = false)
	private UUID uid;

	@Column(name = "name", nullable = false, length = 150)
	@Getter @Setter private String name;

	@Column(name = "start_time", nullable = false)
	@Getter @Setter private ZonedDateTime startTime;

	@Column(name = "end_time", nullable = false)
	@Getter @Setter private ZonedDateTime endTime;

	@Column(name = "frequency", length = 50)
	@Getter @Setter private String frequency;

	@Column(name = "until")
	@Getter @Setter private ZonedDateTime until;
}
