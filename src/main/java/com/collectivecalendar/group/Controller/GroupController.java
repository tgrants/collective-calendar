package com.collectivecalendar.group.Controller;

import com.collectivecalendar.group.Service.GroupService;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.GroupEventRepository;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.InviteRepository;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class GroupController {
    private final GroupService service;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupEventRepository groupEventRepository;
    private final InviteRepository inviteRepository;

    // Pievieno Service
    public GroupController(GroupService service,
                           GroupRepository groupRepository,
                           UserRepository userRepository,
                           UserGroupRepository userGroupRepository,
                           GroupEventRepository groupEventRepository,
                           InviteRepository inviteRepository) {
        this.service = service;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupEventRepository = groupEventRepository;
        this.inviteRepository = inviteRepository;
    }

        @GetMapping("/groups")
        public String getGroups(@AuthenticationPrincipal UserDetails principal, Model model) {
            User currentUser = userRepository.findByUsername(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            List<UserGroup> userGroups = userGroupRepository.findByUserId(currentUser.getUid());
            List<Map<String, Object>> memberships = new ArrayList<>();

            List<String> colors = List.of(
                    "#4f46e5", "#06b6d4", "#22c55e", "#f59e0b", "#ef4444", "#a855f7"
            );

            for (UserGroup userGroup : userGroups) {
                Group group = groupRepository.findById(userGroup.getGroupId())
                        .orElse(null);
                if (group == null) {
                    continue;
                }

                int eventCount = groupEventRepository.findByGroupId(group.getUid()).size();
                int memberCount = userGroupRepository.findByGroupId(group.getUid()).size();
                int colorIndex = Math.abs(group.getUid().hashCode()) % colors.size();
                String color = colors.get(colorIndex);

                Map<String, Object> membership = new HashMap<>();
                membership.put("group", group);
                membership.put("role", userGroup.getRole());
                membership.put("color", color);
                membership.put("eventCount", eventCount);
                membership.put("memberCount", memberCount);

                memberships.add(membership);
            }

            int pendingInviteCount = inviteRepository
                    .findByUserIdAndStatus(currentUser.getUid(), "PENDING")
                    .size();

            model.addAttribute("groups", memberships);
            model.addAttribute("pendingInviteCount", pendingInviteCount);
            return "groups/list";
        }

        /*
        /CREATE
         */

        @GetMapping("/groups/create")
        public String createGroup(Model model) {
            model.addAttribute("group", new Group());
            return "groups/form";
        }

        @PostMapping("/groups/create")
        public String createGroup(@ModelAttribute Group group) {
            service.createGroup(group.getName());
            return "redirect:/groups";
        }

          /*
        GRUPU REDIĢĒŠANA
         */
          @GetMapping("/groups/{id}/edit")
          public String editGroup(@PathVariable String id, Model model) {
              UUID uuid = UUID.fromString(id);

              Group group = groupRepository.findById(uuid)
                      .orElseThrow(() -> new RuntimeException("Group not found: " + id));

              model.addAttribute("group", group);
              return "groups/form";
          }

        @PostMapping("/groups/{id}/edit")
        public String editGroup(@PathVariable String id,
                                @ModelAttribute Group group) {

            service.editGroup(id, group.getName());
            return "redirect:/groups";
        }
          /*
        GRUPU DzĒŠANA
         */
          @PostMapping("/groups/{id}/delete")
          public String deleteGroup(@PathVariable String id) {
              service.deleteGroup(id);
              return "redirect:/groups";
          }



    }

