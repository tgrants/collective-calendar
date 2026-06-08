package com.collectivecalendar.groupUser;

import com.collectivecalendar.model.UserGroup;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.collectivecalendar.groupUser.Service.GroupUserService;

import java.util.List;

@Controller

public class GroupUserController {
    private final GroupUserService groupUserService;

    public GroupUserController(GroupUserService groupUserService) {
        this.groupUserService = groupUserService;
    }
    @GetMapping("/user/{userId}")
    public String getUserGroups(@PathVariable String userId, Model model) {

        List<UserGroup> groups = groupUserService.getGroups(userId);

        model.addAttribute("userGroups", groups);
        model.addAttribute("userId", userId);

        return "groups";
    }
}
