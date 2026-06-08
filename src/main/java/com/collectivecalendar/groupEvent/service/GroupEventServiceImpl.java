package com.collectivecalendar.groupEvent.service;

import com.collectivecalendar.model.GroupEvent;
import com.collectivecalendar.repository.GroupEventRepository;

import java.util.UUID;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupEventServiceImpl implements GroupEventService  {
	private final GroupEventRepository groupEventRepository;
	
	@Override
	public void createGroupEvent(UUID groupId, UUID eventId) {
		GroupEvent groupEvent = new GroupEvent(null, groupId, eventId);
		groupEventRepository.save(groupEvent);
	}
	
	@Override
	public void deleteGroupEvent(UUID groupId, UUID eventId) {
		groupEventRepository.deleteByGroupIdAndEventId(groupId, eventId);
	}
}
