package com.collectivecalendar.notification.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.collectivecalendar.model.User;
import com.collectivecalendar.notification.service.EmailSenderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TestEmailController {

	private final EmailSenderService emailSenderService;

	/*
	 * For testing only
	 */
	@PostMapping("/test-email")
	public String triggerTestEmail(@AuthenticationPrincipal User principal, RedirectAttributes redirectAttributes) {
		String recipientEmail;
		String username;

		if (principal != null && principal.getEmail() != null) {
			recipientEmail = principal.getEmail();
			username = principal.getUsername();
		} else {
			recipientEmail = "dev-fallback@collectivecalendar.local";
			username = "Fallback Developer";
		}

		String subject = "Collective Calendar - SMTP Test Connection";
		String htmlContent = String.format(
				"<h3>SMTP Test Successful!</h3>", 
				username
		);

		boolean isSuccess = emailSenderService.sendHtmlEmail(recipientEmail, subject, htmlContent);

		if (isSuccess) {
			redirectAttributes.addFlashAttribute("successMessage", "Test email successfully sent to " + recipientEmail + "! Check Mailpit.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to send email. Check your application console logs.");
		}

		return "redirect:/";
	}
}
