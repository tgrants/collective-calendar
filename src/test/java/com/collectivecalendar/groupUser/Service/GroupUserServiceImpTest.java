package com.collectivecalendar.groupUser.Service;

import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.Invite;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.InviteRepository;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupUserServiceImpTest {

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private InviteRepository inviteRepository;

    @InjectMocks
    private GroupUserServiceImp groupUserService;

    private UUID userId;
    private UUID groupId;
    private UUID anotherUserId;
    private User testUser;
    private Group testGroup;
    private UserGroup testUserGroup;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        anotherUserId = UUID.randomUUID();

        testUser = User.builder()
                .uid(userId)
                .username("testuser")
                .email("test@example.com")
                .verified(true)
                .password("password")
                .build();

        testGroup = Group.builder()
                .uid(groupId)
                .name("Test Group")
                .build();

        testUserGroup = UserGroup.builder()
                .uid(UUID.randomUUID())
                .userId(userId)
                .groupId(groupId)
                .role("MEMBER")
                .notify(true)
                .build();
    }

    // ============ addUser Tests ============

    @Test
    void testAddUserSuccess() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);

        groupUserService.addUser(userId.toString(), groupId.toString());

        verify(userGroupRepository, times(1)).save(any(UserGroup.class));
    }

    @Test
    void testAddUserUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.addUser(userId.toString(), groupId.toString())
        );
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    @Test
    void testAddUserGroupNotFound() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.addUser(userId.toString(), groupId.toString())
        );
        assertEquals("Group not found with id: " + groupId, exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    @Test
    void testAddUserAlreadyMember() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.addUser(userId.toString(), groupId.toString())
        );
        assertEquals("User is already a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    // ============ removeUser Tests ============

    @Test
    void testRemoveUserSuccess() {
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(true);

        groupUserService.removeUser(userId.toString(), groupId.toString());

        verify(userGroupRepository, times(1)).deleteByUserIdAndGroupId(userId, groupId);
    }

    @Test
    void testRemoveUserNotMember() {
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.removeUser(userId.toString(), groupId.toString())
        );
        assertEquals("User is not a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).deleteByUserIdAndGroupId(any(), any());
    }

    // ============ inviteToGroup Tests ============

    @Test
    void testInviteToGroupSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);
        when(inviteRepository.existsByUserIdAndGroupIdAndStatus(userId, groupId, "PENDING")).thenReturn(false);

        groupUserService.inviteToGroup("testuser", groupId.toString());

        verify(inviteRepository, times(1)).save(any(Invite.class));
    }

    @Test
    void testInviteToGroupUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.inviteToGroup("nonexistent", groupId.toString())
        );
        assertEquals("User not found with username: nonexistent", exception.getMessage());
        verify(inviteRepository, never()).save(any(Invite.class));
    }

    @Test
    void testInviteToGroupGroupNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(groupRepository.existsById(groupId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.inviteToGroup("testuser", groupId.toString())
        );
        assertEquals("Group not found with id: " + groupId, exception.getMessage());
        verify(inviteRepository, never()).save(any(Invite.class));
    }

    @Test
    void testInviteToGroupUserAlreadyMember() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.inviteToGroup("testuser", groupId.toString())
        );
        assertEquals("User is already a member of this group", exception.getMessage());
        verify(inviteRepository, never()).save(any(Invite.class));
    }

    @Test
    void testInviteToGroupPendingInviteExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);
        when(inviteRepository.existsByUserIdAndGroupIdAndStatus(userId, groupId, "PENDING")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.inviteToGroup("testuser", groupId.toString())
        );
        assertEquals("Invite already exists for this user and group", exception.getMessage());
        verify(inviteRepository, never()).save(any(Invite.class));
    }

    // ============ getGroups Tests ============

    @Test
    void testGetGroupsSuccess() {
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(testUserGroup);

        UUID groupId2 = UUID.randomUUID();
        Group group2 = Group.builder().uid(groupId2).name("Group 2").build();
        UserGroup userGroup2 = UserGroup.builder()
                .uid(UUID.randomUUID())
                .userId(userId)
                .groupId(groupId2)
                .role("ADMIN")
                .notify(false)
                .build();
        userGroups.add(userGroup2);

        when(userGroupRepository.findByUserId(userId)).thenReturn(userGroups);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupRepository.findById(groupId2)).thenReturn(Optional.of(group2));

        List<Group> result = groupUserService.getGroups(userId.toString());

        assertEquals(2, result.size());
        assertEquals("Test Group", result.get(0).getName());
        assertEquals("Group 2", result.get(1).getName());
        verify(userGroupRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetGroupsEmpty() {
        when(userGroupRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        List<Group> result = groupUserService.getGroups(userId.toString());

        assertEquals(0, result.size());
        verify(userGroupRepository, times(1)).findByUserId(userId);
    }

    // ============ joinGroup Tests ============

    @Test
    void testJoinGroupSuccess() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);
        when(inviteRepository.findByUserIdAndStatus(userId, "PENDING")).thenReturn(new ArrayList<>());

        groupUserService.joinGroup(userId.toString(), groupId.toString());

        verify(userGroupRepository, times(1)).save(any(UserGroup.class));
    }

    @Test
    void testJoinGroupWithPendingInvite() {
        Invite pendingInvite = Invite.builder()
                .uid(UUID.randomUUID())
                .userId(userId)
                .groupId(groupId)
                .status("PENDING")
                .build();

        List<Invite> invites = new ArrayList<>();
        invites.add(pendingInvite);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);
        when(inviteRepository.findByUserIdAndStatus(userId, "PENDING")).thenReturn(invites);

        groupUserService.joinGroup(userId.toString(), groupId.toString());

        verify(userGroupRepository, times(1)).save(any(UserGroup.class));
        verify(inviteRepository, times(1)).saveAll(any());
        assertEquals("ACCEPTED", pendingInvite.getStatus());
    }

    @Test
    void testJoinGroupUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.joinGroup(userId.toString(), groupId.toString())
        );
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    @Test
    void testJoinGroupAlreadyMember() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.joinGroup(userId.toString(), groupId.toString())
        );
        assertEquals("User is already a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    // ============ leaveGroup Tests ============

    @Test
    void testLeaveGroupSuccess() {
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(true);

        groupUserService.leaveGroup(userId.toString(), groupId.toString());

        verify(userGroupRepository, times(1)).deleteByUserIdAndGroupId(userId, groupId);
    }

    @Test
    void testLeaveGroupNotMember() {
        when(userGroupRepository.existsByUserIdAndGroupId(userId, groupId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.leaveGroup(userId.toString(), groupId.toString())
        );
        assertEquals("User is not a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).deleteByUserIdAndGroupId(any(), any());
    }

    // ============ changeRole Tests ============

    @Test
    void testChangeRoleSuccess() {
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(testUserGroup);

        when(userGroupRepository.findByUserId(userId)).thenReturn(userGroups);

        groupUserService.changeRole(userId.toString(), groupId.toString(), "ADMIN");

        assertEquals("ADMIN", testUserGroup.getRole());
        verify(userGroupRepository, times(1)).save(testUserGroup);
    }

    @Test
    void testChangeRoleUserNotMember() {
        when(userGroupRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.changeRole(userId.toString(), groupId.toString(), "ADMIN")
        );
        assertEquals("User is not a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }

    // ============ changeNotificationSettings Tests ============

    @Test
    void testChangeNotificationSettingsSuccess() {
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(testUserGroup);

        when(userGroupRepository.findByUserId(userId)).thenReturn(userGroups);

        groupUserService.changeNotificationSettings(userId.toString(), groupId.toString(), false);

        assertFalse(testUserGroup.isNotify());
        verify(userGroupRepository, times(1)).save(testUserGroup);
    }

    @Test
    void testChangeNotificationSettingsToTrue() {
        testUserGroup.setNotify(false);
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(testUserGroup);

        when(userGroupRepository.findByUserId(userId)).thenReturn(userGroups);

        groupUserService.changeNotificationSettings(userId.toString(), groupId.toString(), true);

        assertTrue(testUserGroup.isNotify());
        verify(userGroupRepository, times(1)).save(testUserGroup);
    }

    @Test
    void testChangeNotificationSettingsUserNotMember() {
        when(userGroupRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                groupUserService.changeNotificationSettings(userId.toString(), groupId.toString(), false)
        );
        assertEquals("User is not a member of this group", exception.getMessage());
        verify(userGroupRepository, never()).save(any(UserGroup.class));
    }
}
