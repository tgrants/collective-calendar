package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import java.time.ZonedDateTime;
import java.util.List;

public interface EventService {
	/**
	 * Gets the time of next instance of an event at the given point in time.
	 * * @param current_time 	Current date and time.
	 */
	ZonedDateTime getNextInstance(Event event, ZonedDateTime currentTime);
	
	/**
	 * Gets the times of all instances of an event.
	 * * @param current_time 	Current date and time.
	 */
	List<ZonedDateTime> getAllInstances(Event event);
}
