package com.collectivecalendar.group.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.collectivecalendar.group.Service.GroupService;
import com.collectivecalendar.groupUser.Service.GroupUserService;
import com.collectivecalendar.model.Event;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.EventRepository;
import com.collectivecalendar.repository.GroupEventRepository;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.InviteRepository;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;

@Controller
public class GroupController {
	private final GroupService service;
	private final GroupRepository groupRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final UserGroupRepository userGroupRepository;
	private final GroupEventRepository groupEventRepository;
	private final InviteRepository inviteRepository;
	private final GroupUserService groupUserService;

	// Pievieno Service
	public GroupController(GroupService service,
						   GroupRepository groupRepository,
						   EventRepository eventRepository,
						   UserRepository userRepository,
						   UserGroupRepository userGroupRepository,
						   GroupEventRepository groupEventRepository,
						   InviteRepository inviteRepository,
						   GroupUserService groupUserService) {
		this.service = service;
		this.groupRepository = groupRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.userGroupRepository = userGroupRepository;
		this.groupEventRepository = groupEventRepository;
		this.inviteRepository = inviteRepository;
		this.groupUserService = groupUserService;
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
	public String createGroup(@ModelAttribute Group group,
								@AuthenticationPrincipal UserDetails principal) {
		User currentUser = userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

		service.createGroup(group.getName(), currentUser.getUid());
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

	@GetMapping("/groups/{id}")
	public String getGroup(@PathVariable String id,
							@AuthenticationPrincipal UserDetails principal,
							Model model) {
		UUID groupId = UUID.fromString(id);

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Grupa netika atrasta: " + id));

		User currentUser = userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new RuntimeException("Lietotājs netika atrasts"));

		UserGroup currentMembership = userGroupRepository.findByUserId(currentUser.getUid())
				.stream()
				.filter(userGroup -> userGroup.getGroupId().equals(groupId))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Tu neesi grupas dalībnieks"));

		List<Map<String, Object>> members = new ArrayList<>();
		for (UserGroup userGroup : userGroupRepository.findByGroupId(groupId)) {
			User memberUser = userRepository.findById(userGroup.getUserId())
					.orElse(null);
			if (memberUser == null) {
				continue;
			}

			Map<String, Object> member = new HashMap<>();
			member.put("user", memberUser);
			member.put("role", normalizeRole(userGroup.getRole()));
			members.add(member);
		}

		List<Event> events = groupEventRepository.findByGroupId(groupId)
				.stream()
				.map(groupEvent -> eventRepository.findById(groupEvent.getEventId()).orElse(null))
				.filter(java.util.Objects::nonNull)
				.toList();

		model.addAttribute("group", group);
		model.addAttribute("members", members);
		model.addAttribute("events", events);
		model.addAttribute("eventCount", events.size());
		model.addAttribute("memberCount", members.size());
		model.addAttribute("currentUserRole", normalizeRole(currentMembership.getRole()));
		model.addAttribute("currentUserUid", currentUser.getUid());
		return "groups/detail";
	}

	@PostMapping("/groups/{id}/invite")
	public String inviteToGroup(@PathVariable String id,
								@RequestParam String username,
								RedirectAttributes redirectAttributes) {
		try {
			groupUserService.inviteToGroup(username, id);
			redirectAttributes.addFlashAttribute("successMessage",
					"Ielūgums nosūtīts lietotājam " + username + ".");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/groups/" + id;
	}

	@GetMapping("/groups/invites")
	public String getInvites(@AuthenticationPrincipal UserDetails principal, Model model) {
		User currentUser = userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

		List<Map<String, Object>> invites = new ArrayList<>();
		for (var invite : inviteRepository.findByUserIdAndStatus(currentUser.getUid(), "PENDING")) {
			Group inviteGroup = groupRepository.findById(invite.getGroupId()).orElse(null);
			if (inviteGroup == null) {
				continue;
			}

			Map<String, Object> inviteView = new HashMap<>();
			inviteView.put("uid", invite.getUid());
			inviteView.put("createdAt", invite.getCreatedAt());
			inviteView.put("group", inviteGroup);
			invites.add(inviteView);
		}

		model.addAttribute("invites", invites);
		return "groups/invites";
	}

	@PostMapping("/groups/invites/{id}/accept")
	public String acceptInvite(@PathVariable String id,
								@AuthenticationPrincipal UserDetails principal,
								RedirectAttributes redirectAttributes) {
		try {
			User currentUser = userRepository.findByUsername(principal.getUsername())
					.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

			var invite = inviteRepository.findById(UUID.fromString(id))
					.orElseThrow(() -> new RuntimeException("Invite not found: " + id));

			if (!invite.getUserId().equals(currentUser.getUid())) {
				throw new RuntimeException("You cannot accept this invite");
			}

			groupUserService.joinGroup(currentUser.getUid().toString(), invite.getGroupId().toString());
			redirectAttributes.addFlashAttribute("successMessage", "Ielūgums apstiprināts.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/groups/invites";
	}

	@PostMapping("/groups/invites/{id}/decline")
	public String declineInvite(@PathVariable String id,
								@AuthenticationPrincipal UserDetails principal,
								RedirectAttributes redirectAttributes) {
		try {
			User currentUser = userRepository.findByUsername(principal.getUsername())
					.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

			var invite = inviteRepository.findById(UUID.fromString(id))
					.orElseThrow(() -> new RuntimeException("Invite not found: " + id));

			if (!invite.getUserId().equals(currentUser.getUid())) {
				throw new RuntimeException("You cannot decline this invite");
			}

			invite.setStatus("DECLINED");
			inviteRepository.save(invite);
			redirectAttributes.addFlashAttribute("successMessage", "Ielūgums noraidīts.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/groups/invites";
	}

	private String normalizeRole(String role) {
		if ("ADMIN".equals(role) || "EDITOR".equals(role)) {
			return "EDITOR";
		}
		return "VIEWER";
	}

	@PostMapping("/groups/{id}/leave")
	public String leaveGroup(
		@PathVariable String id,
		@AuthenticationPrincipal UserDetails principal,
		RedirectAttributes redirectAttributes
	) {
		try {
			UUID groupId = UUID.fromString(id);

			User currentUser = userRepository.findByUsername(principal.getUsername())
					.orElseThrow(() -> new RuntimeException("Authenticated user not found"));

			UserGroup membership = userGroupRepository.findByUserId(currentUser.getUid())
					.stream()
					.filter(ug -> ug.getGroupId().equals(groupId))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Tu neesi šīs grupas dalībnieks"));

			if ("EDITOR".equals(normalizeRole(membership.getRole()))) {
				throw new IllegalStateException("Redaktori nevar iziet no grupas.");
			}

			userGroupRepository.delete(membership);
			redirectAttributes.addFlashAttribute("successMessage", "Jūs esat izgājuši no grupas.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/groups/" + id;
		}

		return "redirect:/groups";
	}
}