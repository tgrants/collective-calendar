package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventService {
	Event findEvent(UUID eventId);
	void saveEvent(Event event);
	void updateEvent(Event event, UUID eventID);
	void deleteEvent(UUID eventid);
	
	/**
	 * Gets the time of next instance of an event at the given point in time.
	 * * @param current_time 	Current date and time.
	 */
	LocalDateTime getNextInstance(Event event, LocalDateTime currentTime);
	
	/**
	 * Gets the times of all instances of an event.
	 * * @param current_time 	Current date and time.
	 */
	List<LocalDateTime> getInstances(Event event, int count);
}
