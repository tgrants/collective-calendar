package com.collectivecalendar.group.Controller;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.repository.GroupRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.collectivecalendar.group.Service.GroupService;

import java.util.UUID;

@Controller
    public class GroupController {
        private final GroupService service;
        private final GroupRepository groupRepository;

        // Pievieno Service
        public GroupController(GroupService service, GroupRepository groupRepository) {
            this.service = service;
            this.groupRepository = groupRepository;
        }
        // Iegūst sarakstu ar grupām, kam lietotājs pieder
        @GetMapping
        public String groupList (Model model) {
            model.addAttribute("groups", groupRepository.findAll());
            return "groups";
        }
        /*
        /CREATE
         */

        @GetMapping("/groups/create")
        public String createGroup(Model model) {

            model.addAttribute("group", new Group());
            return "form";
        }

        @PostMapping("/groups/create")
        public String createGroup(@ModelAttribute Group group) {
            service.createGroup(group.getName());
            return "redirect:/groups";
        }

          /*
        GRUPU REDIĢĒŠANA
         */
          @GetMapping("/{id}/edit")
          public String editGroup(@PathVariable String id, Model model) {
              UUID uuid = UUID.fromString(id);

              Group group = groupRepository.findById(uuid)
                      .orElseThrow(() -> new RuntimeException("Group not found: " + id));

              model.addAttribute("group", group);
              return "form";
          }

        @PostMapping("/{id}/edit")
        public String editGroup(@PathVariable String id,
                                @ModelAttribute Group group) {

            service.editGroup(id, group.getName());
            return "redirect:/groups";
        }
          /*
        GRUPU DzĒŠANA
         */
          public String deleteGroup(@PathVariable String id) {
              service.deleteGroup(id);
              return "redirect:/groups";
          }

    }

