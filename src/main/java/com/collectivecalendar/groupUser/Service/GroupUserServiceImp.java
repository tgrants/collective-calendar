package com.collectivecalendar.groupUser.Service;

import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.Invite;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.InviteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupUserServiceImp implements GroupUserService {

    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final InviteRepository inviteRepository;

    public GroupUserServiceImp(UserGroupRepository userGroupRepository,
                              UserRepository userRepository,
                              GroupRepository groupRepository,
                              InviteRepository inviteRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.inviteRepository = inviteRepository;
    }

    @Override
    public void addUser(String user_id, String group_id) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Check if user and group exist
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + user_id);
        }
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + group_id);
        }

        // Check if user is already in group
        if (userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        // Create and save new UserGroup
        UserGroup userGroup = UserGroup.builder()
                .userId(userId)
                .groupId(groupId)
                .role("MEMBER")
                .notify(true)
                .build();
        userGroupRepository.save(userGroup);
    }

    @Override
    public void removeUser(String user_id, String group_id) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Check if user is in the group
        if (!userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }

        // Remove user from group
        userGroupRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

    @Override
    public void inviteToGroup(String username, String group_id) {
        UUID groupId = UUID.fromString(group_id);

        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        User user = userOpt.get();

        // Check if group exists
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + group_id);
        }

        // Check if user is already in the group
        if (userGroupRepository.existsByUserIdAndGroupId(user.getUid(), groupId)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        // Check if invite already exists
        if (inviteRepository.existsByUserIdAndGroupIdAndStatus(user.getUid(), groupId, "PENDING")) {
            throw new IllegalArgumentException("Invite already exists for this user and group");
        }

        // Create and save invite
        Invite invite = Invite.builder()
                .userId(user.getUid())
                .groupId(groupId)
                .status("PENDING")
                .build();
        inviteRepository.save(invite);
    }


    @Override
    public List<Group> getGroups(String user_id) {
        UUID userId = UUID.fromString(user_id);
        
        // Get all UserGroup records for this user
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        
        // Extract group IDs and fetch the actual groups
        return userGroups.stream()
                .map(UserGroup::getGroupId)
                .map(groupRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void joinGroup(String user_id, String group_id) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Check if user and group exist
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + user_id);
        }
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + group_id);
        }

        // Check if user is already in the group
        if (userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        // Add user to group
        UserGroup userGroup = UserGroup.builder()
                .userId(userId)
                .groupId(groupId)
                .role("MEMBER")
                .notify(true)
                .build();
        userGroupRepository.save(userGroup);

        // Remove any pending invite
        List<Invite> invites = inviteRepository.findByUserIdAndStatus(userId, "PENDING");
        invites.stream()
                .filter(invite -> invite.getGroupId().equals(groupId))
                .forEach(invite -> invite.setStatus("ACCEPTED"));
        inviteRepository.saveAll(invites);
    }

    @Override
    public void leaveGroup(String user_id, String group_id) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Check if user is in the group
        if (!userGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }

        // Remove user from group
        userGroupRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

    @Override
    public void changeRole(String user_id, String group_id, String role) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Find the UserGroup record
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        UserGroup userGroup = userGroups.stream()
                .filter(ug -> ug.getGroupId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        // Update role
        userGroup.setRole(role);
        userGroupRepository.save(userGroup);
    }

    @Override
    public void changeNotificationSettings(String user_id, String group_id, boolean notify) {
        UUID userId = UUID.fromString(user_id);
        UUID groupId = UUID.fromString(group_id);

        // Find the UserGroup record
        List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);
        UserGroup userGroup = userGroups.stream()
                .filter(ug -> ug.getGroupId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        // Update notification settings
        userGroup.setNotify(notify);
        userGroupRepository.save(userGroup);
    }
}
