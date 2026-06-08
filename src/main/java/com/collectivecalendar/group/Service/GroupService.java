package com.collectivecalendar.group.Service;

import org.springframework.stereotype.Service;


public interface GroupService {
    void editGroup(String group_id, String group_name);
    void createGroup (String group_name);
    void deleteGroup (String group_id);

}
