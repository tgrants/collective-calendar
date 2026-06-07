package com.collectivecalendar.event.service;

public interface EventService {
	/**
	 * Gets the time of next instance of an event at the given point in time.
	 * * @param current_time 	Current date and time.
	 */
	ZonedDateTime getNextInstance(ZonedDateTime current_time);
	
	/**
	 * Gets the times of all instances of an event after the given point in time.
	 * * @param current_time 	Current date and time.
	 */
	List<ZonedDateTime> getAllInstances(ZonedDateTime current_time);
}
