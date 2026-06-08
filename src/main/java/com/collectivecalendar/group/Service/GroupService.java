package com.collectivecalendar.group.Service;

import java.util.UUID;

public interface GroupService {
	void editGroup(String group_id, String group_name);
	void createGroup(String group_name, UUID currentUserId);
	void deleteGroup (String group_id);
}
