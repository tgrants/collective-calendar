package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;

import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService  {

	private final Event event;
	
	@Override
	ZonedDateTime getNextInstance(ZonedDateTime current_time) {
		String frequencyString = event.getFrequency();
		//
		
		return ZonedDateTime.now();;
		
	}

	@Override
	List<ZonedDateTime> getAllInstances(ZonedDateTime current_time) {
		return ZonedDateTime.now();
	}
}
