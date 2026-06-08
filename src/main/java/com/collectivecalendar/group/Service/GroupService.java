package com.collectivecalendar.group.Service;

import java.util.UUID;

import org.springframework.stereotype.Service;


public interface GroupService {
    void editGroup(String group_id, String group_name);
    void createGroup(String group_name, UUID currentUserId);
    void deleteGroup (String group_id);

}
