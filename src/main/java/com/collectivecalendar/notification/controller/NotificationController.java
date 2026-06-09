package com.collectivecalendar.notification.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.collectivecalendar.model.Notification;
import com.collectivecalendar.model.User;
import com.collectivecalendar.repository.NotificationRepository;
import com.collectivecalendar.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	@GetMapping
	public String showNotificationsPage(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		User user = userRepository.findByUsername(principal.getName())
			.orElseThrow(() -> new RuntimeException("Logged in user context not found"));

		List<Notification> notifications = notificationRepository
			.findByNotifyUserUid(user.getUid());

		model.addAttribute("notifications", notifications);
		return "notifications/list";
	}

	@PostMapping("/mark-all-seen")
	public String markAllNotificationsAsSeen(Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		User user = userRepository.findByUsername(principal.getName())
			.orElseThrow(() -> new RuntimeException("Logged in user context not found"));

		List<Notification> unreadNotifications = notificationRepository
			.findByNotifyUserUidAndSeenFalse(user.getUid());

		if (!unreadNotifications.isEmpty()) {
			for (Notification notification : unreadNotifications) {
				notification.setSeen(true);
			}
			notificationRepository.saveAll(unreadNotifications);
			log.info("Marked {} notifications as read for user {}", unreadNotifications.size(), user.getUsername());
		}

		return "redirect:/notifications";
	}
}
