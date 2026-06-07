package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.LinkedList;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService  {

	private final Event event = null;
	
	@Override
	public ZonedDateTime getNextInstance(ZonedDateTime currentTime) {
		List<ZonedDateTime> allInstances = getAllInstances();
		// Iterator<ZonedDateTime> currentInstance = null;
		
		//
		
		return ZonedDateTime.now();
		
	}

	// TODO do this
	@Override
	public List<ZonedDateTime> getAllInstances() {
		List<ZonedDateTime> timeList = new LinkedList<>();
		ZonedDateTime startTime = event.getStartTime();
		ZonedDateTime endTime = event.getEndTime();
		String frequencyString = event.getFrequency();
		int frequencyInt = Integer.parseInt(frequencyString.substring(1));
		
		switch(frequencyString.charAt(0)) {
		case 'd':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime.plusDays(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'w':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime.plusWeeks(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'm':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime.plusMonths(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		default:
			break;
		}
		
		return timeList;
	}
}
