package com.collectivecalendar.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.collectivecalendar.model.Event;

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
	 * Gets the times of a set number of instances for an event.
	 * * @param current_time 	Current date and time.
	 * * @param count			Count of instances.
	 */
	List<LocalDateTime> getInstances(Event event, int count);
}
