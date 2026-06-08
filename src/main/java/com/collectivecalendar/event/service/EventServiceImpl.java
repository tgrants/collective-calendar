package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import com.collectivecalendar.repository.EventRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService  {
	private final EventRepository eventRepository;
	
	@Override
	public Event findEvent(UUID eventId) {
		Event event = eventRepository.findById(eventId).orElseThrow();
		return event;
	}
	
	@Override
	public void saveEvent(Event event) {
		switch(event.getFrequency()) {
		case "DAILY":
			event.setFrequency("d1");
			break;
		case "WEEKLY":
			event.setFrequency("w1");
			break;
		case "MONTHLY":
			event.setFrequency("m1");
			break;
		default:
			event.setFrequency(" ");
			break;
		}
		eventRepository.save(event);
	}
	
	@Override
	public void updateEvent(Event event, UUID eventId) {
		Event existingEvent = eventRepository.findById(eventId).orElseThrow();
		
		existingEvent.setName(event.getName());
		existingEvent.setStartTime(event.getStartTime());
		existingEvent.setEndTime(event.getEndTime());
		switch(event.getFrequency()) {
		case "DAILY":
			existingEvent.setFrequency("d1");
			break;
		case "WEEKLY":
			existingEvent.setFrequency("w1");
			break;
		case "MONTHLY":
			existingEvent.setFrequency("m1");
			break;
		default:
			existingEvent.setFrequency(" ");
			break;
		}
		existingEvent.setFrequency(event.getFrequency());

		eventRepository.save(existingEvent);
	}
	
	@Override
	public void deleteEvent(UUID eventId) {
		eventRepository.deleteById(eventId);
	}
	
	@Override
	public ZonedDateTime getNextInstance(Event event, ZonedDateTime currentTime) {
		List<ZonedDateTime> allInstances = getAllInstances(event);
		Iterator<ZonedDateTime> currentInstance = allInstances.iterator();
		
		while (currentInstance.hasNext()) {
			ZonedDateTime tempTime = currentInstance.next();
			if (tempTime.isAfter(currentTime)) {
					return tempTime;
				}
			}
				
		return null;
		
	}

	@Override
	public List<ZonedDateTime> getAllInstances(Event event) {
		List<ZonedDateTime> timeList = new ArrayList<>();
		ZonedDateTime startTime = event.getStartTime();
		ZonedDateTime untilTime = event.getUntil();
		String frequencyString = event.getFrequency();
		int frequencyInt = Integer.parseInt(frequencyString.substring(1));
		
		switch(frequencyString.charAt(0)) {
		case 'd':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(untilTime); tempTime = tempTime.plusDays(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'w':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(untilTime); tempTime = tempTime.plusWeeks(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'm':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(untilTime); tempTime = tempTime.plusMonths(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		default:
			timeList.add(startTime);
			break;
		}
		
		return timeList;
	}
}
