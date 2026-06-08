package com.collectivecalendar.groupUser.Service;

import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class GroupUserServiceImp implements GroupUserService {

    private final UserGroupRepository userGroupRepository;

    public GroupUserServiceImp(UserGroupRepository userGroupRepository) {
            this.userGroupRepository = userGroupRepository;
        }
    @Override
    public void addUser(String user_id, String group_id) {

    }

    @Override
    public void removeUser(String user_id) {

    }

    @Override
    public void inviteToGroup(String group_id, String username) {

    }

    @Override
    public List<UserGroup> getGroups(String user_id) {
        UUID userId = UUID.fromString(user_id);
        return userGroupRepository.findByUserId(userId);
    }

    @Override
    public void joinGroup(String group_id, String user_id) {

    }

    @Override
    public void leaveGroup(String group_id, String user_id) {

    }
}
