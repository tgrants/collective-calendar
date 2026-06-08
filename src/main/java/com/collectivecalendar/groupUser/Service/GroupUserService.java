package com.collectivecalendar.groupUser.Service;

import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.model.Group;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GroupUserService {

    // adds user to group
    void addUser(String user_id, String group_id);
    
    // removes user from group
    void removeUser(String user_id, String group_id);
    
    // invites a user to a group
    void inviteToGroup(String username, String group_id);
    
    // gets all groups for a user
    List<Group> getGroups(String user_id);
    
    // user joins a group
    void joinGroup(String user_id, String group_id);
    
    // user leaves a group
    void leaveGroup(String user_id, String group_id);
    
    // changes user's role in a group
    void changeRole(String user_id, String group_id, String role);
    
    // changes notification settings for user in a group
    void changeNotificationSettings(String user_id, String group_id, boolean notify);
}
