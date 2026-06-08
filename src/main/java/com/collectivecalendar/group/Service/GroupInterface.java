package com.collectivecalendar.group.Service;

public interface GroupInterface {
    // adds user to group
    void addUser(String user_id);
    // removes user from group
    void removeUser(String user_id);
    // edits group properties
    void editGroup(String group_id);
}
