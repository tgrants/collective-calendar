package com.collectivecalendar.event.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.collectivecalendar.model.Event;
import com.collectivecalendar.repository.EventRepository;

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
		eventRepository.save(event);
	}

	@Override
	public void updateEvent(Event event, UUID eventId) {
		Event existingEvent = eventRepository.findById(eventId).orElseThrow();
		
		existingEvent.setName(event.getName());
		existingEvent.setStartTime(event.getStartTime());
		existingEvent.setEndTime(event.getEndTime());
		existingEvent.setFrequency(event.getFrequency());
		existingEvent.setUntil(event.getUntil());

		eventRepository.save(existingEvent);
	}

	@Override
	public void deleteEvent(UUID eventId) {
		eventRepository.deleteById(eventId);
	}

	@Override
	public LocalDateTime getNextInstance(Event event, LocalDateTime currentTime) {
		List<LocalDateTime> instances = getInstances(event, 365);
		Iterator<LocalDateTime> currentInstance = instances.iterator();
		
		while (currentInstance.hasNext()) {
			LocalDateTime tempTime = currentInstance.next();
			if (tempTime.isAfter(currentTime)) {
				return tempTime;
			}
		}
				
		return null;
	}

	@Override
	public List<LocalDateTime> getInstances(Event event, int count) {
		List<LocalDateTime> timeList = new ArrayList<>();
		LocalDateTime startTime = event.getStartTime();
		LocalDateTime untilTime;
		if (event.getUntil() != null) {
			untilTime = event.getUntil();
		} else {
			untilTime = LocalDateTime.now().plusYears(100);
		}
		String frequencyString = event.getFrequency();

		switch(frequencyString) {
		case "DAILY":
			for (LocalDateTime tempTime = startTime; !tempTime.isAfter(untilTime) && count > 0; tempTime = tempTime.plusDays(1)) {
				timeList.add(tempTime);
				count--;
			}
			break;
		case "WEEKLY":
			for (LocalDateTime tempTime = startTime; !tempTime.isAfter(untilTime) && count > 0; tempTime = tempTime.plusWeeks(1)) {
				timeList.add(tempTime);
				count--;
			}
			break;
		case "MONTHLY":
			for (LocalDateTime tempTime = startTime; !tempTime.isAfter(untilTime) && count > 0; tempTime = tempTime.plusMonths(1)) {
				timeList.add(tempTime);
				count--;
			}
			break;
		default:
			timeList.add(startTime);
			break;
		}

		return timeList;
	}
}
