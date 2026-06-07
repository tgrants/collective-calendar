package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService  {

	private final Event event = null;
	
	@Override
	public ZonedDateTime getNextInstance(ZonedDateTime current_time) {
		String frequencyString = event.getFrequency();
		//
		
		return ZonedDateTime.now();
		
	}

	@Override
	public List<ZonedDateTime> getAllInstances(ZonedDateTime current_time) {
		return null;
	}
}
