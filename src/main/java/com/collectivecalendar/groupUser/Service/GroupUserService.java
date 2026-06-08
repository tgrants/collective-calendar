package com.collectivecalendar.groupUser.Service;

import com.collectivecalendar.model.UserGroup;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GroupUserService {

    // adds user to group
    void addUser(String user_id, String group_id);
    // removes user from group
    void removeUser(String user_id);
    // edits group properties
    void inviteToGroup(String group_id, String username);
    List<UserGroup> getGroups(String user_id);
    void joinGroup(String group_id, String user_id);
    void leaveGroup(String group_id, String user_id);
}
