package com.collectivecalendar.groupEvent.service;

import com.collectivecalendar.model.GroupEvent;
import java.util.UUID;

public interface GroupEventService {
	void createGroupEvent(UUID groupId, UUID eventId);
	void deleteGroupEvent(UUID groupId, UUID eventId);
}
