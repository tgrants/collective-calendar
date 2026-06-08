package com.collectivecalendar.event.service;

import com.collectivecalendar.model.Event;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.stereotype.Service;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class EventServiceImpl implements EventService  {
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
		ZonedDateTime endTime = event.getEndTime();
		String frequencyString = event.getFrequency();
		int frequencyInt = Integer.parseInt(frequencyString.substring(1));
		
		switch(frequencyString.charAt(0)) {
		case 'd':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime = tempTime.plusDays(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'w':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime = tempTime.plusWeeks(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		case 'm':
			for (ZonedDateTime tempTime = startTime; !tempTime.isAfter(endTime); tempTime = tempTime.plusMonths(frequencyInt)) {
				timeList.add(tempTime);
			}
			break;
		default:
			break;
		}
		
		return timeList;
	}
}
