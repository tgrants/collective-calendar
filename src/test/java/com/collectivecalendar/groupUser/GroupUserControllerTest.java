package com.collectivecalendar.groupUser;

import com.collectivecalendar.model.Group;
import com.collectivecalendar.groupUser.Service.GroupUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupUserControllerTest {

    @Mock
    private GroupUserService groupUserService;

    @InjectMocks
    private GroupUserController groupUserController;

    private UUID userId;
    private UUID groupId;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        testGroup = Group.builder()
                .uid(groupId)
                .name("Test Group")
                .build();
    }

    // ============ getUserGroups Tests ============

    @Test
    void testGetUserGroupsSuccess() {
        List<Group> groups = Arrays.asList(testGroup);
        when(groupUserService.getGroups(userId.toString())).thenReturn(groups);

        ResponseEntity<List<Group>> response = groupUserController.getUserGroups(userId.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Group", response.getBody().get(0).getName());
        verify(groupUserService, times(1)).getGroups(userId.toString());
    }

    @Test
    void testGetUserGroupsEmpty() {
        when(groupUserService.getGroups(userId.toString())).thenReturn(new ArrayList<>());

        ResponseEntity<List<Group>> response = groupUserController.getUserGroups(userId.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetUserGroupsThrowsException() {
        when(groupUserService.getGroups("invalid")).thenThrow(new IllegalArgumentException("Invalid UUID"));

        ResponseEntity<List<Group>> response = groupUserController.getUserGroups("invalid");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ============ addUser Tests ============

    @Test
    void testAddUserSuccess() {
        Map<String, String> request = new HashMap<>();
        request.put("userId", userId.toString());
        request.put("groupId", groupId.toString());

        doNothing().when(groupUserService).addUser(userId.toString(), groupId.toString());

        ResponseEntity<String> response = groupUserController.addUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User added to group successfully", response.getBody());
        verify(groupUserService, times(1)).addUser(userId.toString(), groupId.toString());
    }

    @Test
    void testAddUserUserNotFound() {
        Map<String, String> request = new HashMap<>();
        request.put("userId", userId.toString());
        request.put("groupId", groupId.toString());

        doThrow(new IllegalArgumentException("User not found"))
                .when(groupUserService).addUser(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.addUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testAddUserAlreadyMember() {
        Map<String, String> request = new HashMap<>();
        request.put("userId", userId.toString());
        request.put("groupId", groupId.toString());

        doThrow(new IllegalArgumentException("User is already a member of this group"))
                .when(groupUserService).addUser(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.addUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is already a member of this group", response.getBody());
    }

    // ============ removeUser Tests ============

    @Test
    void testRemoveUserSuccess() {
        doNothing().when(groupUserService).removeUser(userId.toString(), groupId.toString());

        ResponseEntity<String> response = groupUserController.removeUser(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed from group successfully", response.getBody());
        verify(groupUserService, times(1)).removeUser(userId.toString(), groupId.toString());
    }

    @Test
    void testRemoveUserNotMember() {
        doThrow(new IllegalArgumentException("User is not a member of this group"))
                .when(groupUserService).removeUser(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.removeUser(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a member of this group", response.getBody());
    }

    // ============ inviteToGroup Tests ============

    @Test
    void testInviteToGroupSuccess() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("groupId", groupId.toString());

        doNothing().when(groupUserService).inviteToGroup("testuser", groupId.toString());

        ResponseEntity<String> response = groupUserController.inviteToGroup(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Invite sent successfully", response.getBody());
        verify(groupUserService, times(1)).inviteToGroup("testuser", groupId.toString());
    }

    @Test
    void testInviteToGroupUserNotFound() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "nonexistent");
        request.put("groupId", groupId.toString());

        doThrow(new IllegalArgumentException("User not found with username: nonexistent"))
                .when(groupUserService).inviteToGroup(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.inviteToGroup(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found with username: nonexistent", response.getBody());
    }

    // ============ joinGroup Tests ============

    @Test
    void testJoinGroupSuccess() {
        doNothing().when(groupUserService).joinGroup(userId.toString(), groupId.toString());

        ResponseEntity<String> response = groupUserController.joinGroup(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User joined group successfully", response.getBody());
        verify(groupUserService, times(1)).joinGroup(userId.toString(), groupId.toString());
    }

    @Test
    void testJoinGroupAlreadyMember() {
        doThrow(new IllegalArgumentException("User is already a member of this group"))
                .when(groupUserService).joinGroup(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.joinGroup(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is already a member of this group", response.getBody());
    }

    // ============ leaveGroup Tests ============

    @Test
    void testLeaveGroupSuccess() {
        doNothing().when(groupUserService).leaveGroup(userId.toString(), groupId.toString());

        ResponseEntity<String> response = groupUserController.leaveGroup(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User left group successfully", response.getBody());
        verify(groupUserService, times(1)).leaveGroup(userId.toString(), groupId.toString());
    }

    @Test
    void testLeaveGroupNotMember() {
        doThrow(new IllegalArgumentException("User is not a member of this group"))
                .when(groupUserService).leaveGroup(anyString(), anyString());

        ResponseEntity<String> response = groupUserController.leaveGroup(userId.toString(), groupId.toString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a member of this group", response.getBody());
    }

    // ============ changeRole Tests ============

    @Test
    void testChangeRoleSuccess() {
        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        doNothing().when(groupUserService).changeRole(userId.toString(), groupId.toString(), "ADMIN");

        ResponseEntity<String> response = groupUserController.changeRole(userId.toString(), groupId.toString(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Role changed successfully", response.getBody());
        verify(groupUserService, times(1)).changeRole(userId.toString(), groupId.toString(), "ADMIN");
    }

    @Test
    void testChangeRoleUserNotMember() {
        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        doThrow(new IllegalArgumentException("User is not a member of this group"))
                .when(groupUserService).changeRole(anyString(), anyString(), anyString());

        ResponseEntity<String> response = groupUserController.changeRole(userId.toString(), groupId.toString(), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a member of this group", response.getBody());
    }

    // ============ changeNotificationSettings Tests ============

    @Test
    void testChangeNotificationSettingsSuccess() {
        Map<String, Boolean> request = new HashMap<>();
        request.put("notify", false);

        doNothing().when(groupUserService).changeNotificationSettings(userId.toString(), groupId.toString(), false);

        ResponseEntity<String> response = groupUserController.changeNotificationSettings(userId.toString(), groupId.toString(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification settings updated successfully", response.getBody());
        verify(groupUserService, times(1)).changeNotificationSettings(userId.toString(), groupId.toString(), false);
    }

    @Test
    void testChangeNotificationSettingsMissingNotifyParameter() {
        Map<String, Boolean> request = new HashMap<>();
        request.put("someOtherField", true);

        ResponseEntity<String> response = groupUserController.changeNotificationSettings(userId.toString(), groupId.toString(), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Notify parameter is required", response.getBody());
    }

    @Test
    void testChangeNotificationSettingsUserNotMember() {
        Map<String, Boolean> request = new HashMap<>();
        request.put("notify", true);

        doThrow(new IllegalArgumentException("User is not a member of this group"))
                .when(groupUserService).changeNotificationSettings(anyString(), anyString(), anyBoolean());

        ResponseEntity<String> response = groupUserController.changeNotificationSettings(userId.toString(), groupId.toString(), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not a member of this group", response.getBody());
    }
}
