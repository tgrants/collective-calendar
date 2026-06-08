package com.collectivecalendar.groupUser;

public interface GroupUserService {

    // adds user to group
    void addUser(String user_id, String group_id);
    // removes user from group
    void removeUser(String user_id);
    // edits group properties
    void inviteToGroup(String group_id, String user_id);
}
