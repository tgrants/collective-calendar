package com.collectivecalendar.user.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.collectivecalendar.model.User;
import com.collectivecalendar.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {

	private final UserService userService;

	@GetMapping
	public String showSettingsPage(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		User user = userService.getByUsername(principal.getName());
		model.addAttribute("user", user);
		return "users/settings";
	}

	@PostMapping("/notifications")
	public String saveNotificationSettings(
			@RequestParam(value = "calendarNotify", required = false) String calendarNotifyParam,
			@RequestParam(value = "inviteNotify", required = false) String inviteNotifyParam,
			Principal principal,
			RedirectAttributes redirectAttributes) {

		if (principal == null) {
			return "redirect:/login";
		}

		boolean calendarNotify = "on".equals(calendarNotifyParam);
		boolean inviteNotify = "on".equals(inviteNotifyParam);

		try {
			userService.updateNotificationSettings(principal.getName(), calendarNotify, inviteNotify);
			redirectAttributes.addFlashAttribute("successMessage", "Paziņojumu iestatījumi saglabāti.");
		} catch (Exception e) {
			log.error("Error saving notification preferences", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Kļūda saglabājot iestatījumus.");
		}

		return "redirect:/settings";
	}

	@PostMapping("/change-password")
	public String changePassword(
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			Principal principal,
			RedirectAttributes redirectAttributes) {

		if (principal == null) {
			return "redirect:/login";
		}

		try {
			userService.changePassword(principal.getName(), currentPassword, newPassword, confirmPassword);
			redirectAttributes.addFlashAttribute("successMessage", "Parole veiksmīgi nomainīta.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected password alteration exception tracking", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Sistēmas kļūda, mēģiniet vēlreiz.");
		}

		return "redirect:/settings";
	}

	@PostMapping("/send-verification")
	public String triggerVerification(Principal principal, RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/login";
		}

		try {
			userService.sendVerificationEmail(principal.getName());
			redirectAttributes.addFlashAttribute("successMessage", "Verifikācijas e-pasts nosūtīts!");
		} catch (Exception e) {
			log.error("Verification email routing failure", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Neizdevās nosūtīt e-pastu.");
		}

		return "redirect:/settings";
	}
}
