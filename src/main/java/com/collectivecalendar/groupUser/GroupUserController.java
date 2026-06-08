package com.collectivecalendar.groupUser;

import com.collectivecalendar.model.Group;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.collectivecalendar.groupUser.Service.GroupUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-users")
public class GroupUserController {
    private final GroupUserService groupUserService;

    public GroupUserController(GroupUserService groupUserService) {
        this.groupUserService = groupUserService;
    }

    /**
     * Get all groups for a user
     * GET /api/group-users/{userId}/groups
     */
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<Group>> getUserGroups(@PathVariable String userId) {
        try {
            List<Group> groups = groupUserService.getGroups(userId);
            return ResponseEntity.ok(groups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add user to a group (admin action)
     * POST /api/group-users/add
     * Body: { "userId": "...", "groupId": "..." }
     */
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String groupId = request.get("groupId");
            groupUserService.addUser(userId, groupId);
            return ResponseEntity.status(HttpStatus.CREATED).body("User added to group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove user from a group
     * DELETE /api/group-users/{userId}/groups/{groupId}
     */
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> removeUser(@PathVariable String userId, @PathVariable String groupId) {
        try {
            groupUserService.removeUser(userId, groupId);
            return ResponseEntity.ok("User removed from group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Invite a user to a group
     * POST /api/group-users/invite
     * Body: { "username": "...", "groupId": "..." }
     */
    @PostMapping("/invite")
    public ResponseEntity<String> inviteToGroup(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String groupId = request.get("groupId");
            groupUserService.inviteToGroup(username, groupId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Invite sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * User joins a group
     * POST /api/group-users/{userId}/join/{groupId}
     */
    @PostMapping("/{userId}/join/{groupId}")
    public ResponseEntity<String> joinGroup(@PathVariable String userId, @PathVariable String groupId) {
        try {
            groupUserService.joinGroup(userId, groupId);
            return ResponseEntity.status(HttpStatus.CREATED).body("User joined group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * User leaves a group
     * POST /api/group-users/{userId}/leave/{groupId}
     */
    @PostMapping("/{userId}/leave/{groupId}")
    public ResponseEntity<String> leaveGroup(@PathVariable String userId, @PathVariable String groupId) {
        try {
            groupUserService.leaveGroup(userId, groupId);
            return ResponseEntity.ok("User left group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Change user's role in a group
     * PUT /api/group-users/{userId}/groups/{groupId}/role
     * Body: { "role": "ADMIN" }
     */
    @PutMapping("/{userId}/groups/{groupId}/role")
    public ResponseEntity<String> changeRole(@PathVariable String userId, 
                                            @PathVariable String groupId, 
                                            @RequestBody Map<String, String> request) {
        try {
            String role = request.get("role");
            groupUserService.changeRole(userId, groupId, role);
            return ResponseEntity.ok("Role changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Change user's notification settings for a group
     * PUT /api/group-users/{userId}/groups/{groupId}/notifications
     * Body: { "notify": true }
     */
    @PutMapping("/{userId}/groups/{groupId}/notifications")
    public ResponseEntity<String> changeNotificationSettings(@PathVariable String userId, 
                                                            @PathVariable String groupId, 
                                                            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean notify = request.get("notify");
            if (notify == null) {
                return ResponseEntity.badRequest().body("Notify parameter is required");
            }
            groupUserService.changeNotificationSettings(userId, groupId, notify);
            return ResponseEntity.ok("Notification settings updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
