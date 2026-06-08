package com.collectivecalendar.event.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.collectivecalendar.model.User;
import com.collectivecalendar.notification.service.EmailSenderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EventController {
	@PostMapping("/test-event")
	public String triggerEvent() {
		System.out.println("Hello, world!");
		
		return "";
	}
}
