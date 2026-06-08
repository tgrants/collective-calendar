package com.collectivecalendar.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
	Optional<Event> findById(UUID eventId);
	void deleteById(UUID eventId);
	// List<Event> findAllById(Set<UUID> eventIds);
	List<Event> findAll();
}
