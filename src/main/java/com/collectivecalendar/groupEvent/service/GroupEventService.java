package com.collectivecalendar.groupEvent.service;

import java.util.UUID;

public interface GroupEventService {
	void createGroupEvent(UUID groupId, UUID eventId);
	void deleteGroupEvent(UUID groupId, UUID eventId);
}
