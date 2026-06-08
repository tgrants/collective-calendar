package com.collectivecalendar.model;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
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
	@Getter private UUID uid;

	@Column(name = "name", nullable = false, length = 150)
	@Getter @Setter private String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Column(name = "start_time", nullable = false)
	@Getter @Setter private LocalDateTime startTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Column(name = "end_time", nullable = false)
	@Getter @Setter private LocalDateTime endTime;

	@Column(name = "frequency", length = 50)
	@Getter @Setter private String frequency;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Column(name = "until")
	@Getter @Setter private LocalDateTime until;
}
