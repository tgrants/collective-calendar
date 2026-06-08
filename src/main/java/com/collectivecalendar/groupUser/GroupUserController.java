package com.collectivecalendar.groupUser;

import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.collectivecalendar.groupUser.Service.GroupUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-users")
public class GroupUserController {
    private final GroupUserService groupUserService;
    private final UserRepository userRepository;

    public GroupUserController(GroupUserService groupUserService, UserRepository userRepository) {
        this.groupUserService = groupUserService;
        this.userRepository = userRepository;
    }

    private String getCurrentUserId(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Unauthenticated user");
        }
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUid()
                .toString();
    }

    /**
     * Get all groups for the current logged-in user
     * GET /api/group-users/groups
     */
    @GetMapping("/groups")
    public String getUserGroups(String user_id, Model model)
    {
        List<Group> groups = groupUserService.getGroups(user_id);

        model.addAttribute("userGroups", groups);
        model.addAttribute("userId", user_id);

        return "groups/list";

    }

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
     * Remove current user from a group
     * DELETE /api/group-users/groups/{groupId}
     */
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<String> removeUser(@AuthenticationPrincipal UserDetails principal, @PathVariable String groupId) {
        try {
            String userId = getCurrentUserId(principal);
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
     * POST /api/group-users/join/{groupId}
     */
    @PostMapping("/join/{groupId}")
    public ResponseEntity<String> joinGroup(@AuthenticationPrincipal UserDetails principal, @PathVariable String groupId) {
        try {
            String userId = getCurrentUserId(principal);
            groupUserService.joinGroup(userId, groupId);
            return ResponseEntity.status(HttpStatus.CREATED).body("User joined group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * User leaves a group
     * POST /api/group-users/leave/{groupId}
     */
    @PostMapping("/leave/{groupId}")
    public ResponseEntity<String> leaveGroup(@AuthenticationPrincipal UserDetails principal, @PathVariable String groupId) {
        try {
            String userId = getCurrentUserId(principal);
            groupUserService.leaveGroup(userId, groupId);
            return ResponseEntity.ok("User left group successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Change user's role in a group
     * PUT /api/group-users/groups/{groupId}/role
     * Body: { "role": "ADMIN" }
     */
    @PutMapping("/groups/{groupId}/role")
    public ResponseEntity<String> changeRole(@AuthenticationPrincipal UserDetails principal,
                                            @PathVariable String groupId,
                                            @RequestBody Map<String, String> request) {
        try {
            String userId = getCurrentUserId(principal);
            String role = request.get("role");
            groupUserService.changeRole(userId, groupId, role);
            return ResponseEntity.ok("Role changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Change user's notification settings for a group
     * PUT /api/group-users/groups/{groupId}/notifications
     * Body: { "notify": true }
     */
    @PutMapping("/groups/{groupId}/notifications")
    public ResponseEntity<String> changeNotificationSettings(@AuthenticationPrincipal UserDetails principal,
                                                            @PathVariable String groupId,
                                                            @RequestBody Map<String, Boolean> request) {
        try {
            String userId = getCurrentUserId(principal);
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
