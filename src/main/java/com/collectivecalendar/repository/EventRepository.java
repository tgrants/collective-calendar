package com.collectivecalendar.repository;

import java.util.UUID;
import java.util.Optional;
import com.collectivecalendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
	Optional<Event> findById(UUID eventId);
	void deleteById(UUID eventId);
}
