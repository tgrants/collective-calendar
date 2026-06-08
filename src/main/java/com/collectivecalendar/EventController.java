package com.collectivecalendar;

import com.collectivecalendar.model.Event;
import com.collectivecalendar.event.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import lombok.RequiredArgsConstructor;
import java.time.ZonedDateTime;

@Controller
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;
	
	@GetMapping("/groups/{group_id}/events/create")
    public String createEvent(@PathVariable UUID group_id, Model model) {
		Event event = new Event();
		
		model.addAttribute("groupUid", group_id);
		model.addAttribute("event", event);
		
        return "events/form";
    }
	
	/*
	@PostMapping(value = "/groups/{group_id}/events/create", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String saveEvent(@ModelAttribute Event event, @PathVariable UUID group_id, Model model) {
		eventService.saveEvent(event);
		
        return "redirect:/calendar";
    }
    */
	@PostMapping(value = "/groups/{group_id}/events/create", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String saveEvent(@PathVariable UUID group_id, Model model) {
		Event event = new Event(null, "Notikums", ZonedDateTime.now(), ZonedDateTime.now().plusHours(1), "WEEKLY", ZonedDateTime.now().plusDays(15));
		eventService.saveEvent(event);
		System.out.println(event.toString());
		
        return "redirect:/calendar";
    }
	
	@GetMapping("/groups/{group_id}/events/{event_id}/edit")
	public String editEvent(@PathVariable UUID group_id, @PathVariable UUID event_id, Model model) {
		Event event = eventService.findEvent(event_id);
		
		model.addAttribute("groupUid", group_id);
		model.addAttribute("event", event);
		
		return "events/form";
	}
	
	/*
	@PostMapping(value = "/groups/{group_id}/events/{event_id}/create", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public String updateEvent(@ModelAttribute Event event, @PathVariable UUID group_id, @PathVariable UUID event_id, Model model) {
		eventService.saveEvent(event);
		
        return "redirect:/calendar";
    }
    */
	@PostMapping(value = "/groups/{group_id}/events/{event_id}/edit", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public String updateEvent(@PathVariable UUID group_id, @PathVariable UUID event_id, Model model) {
		Event event = new Event(null, "Tas pats notikums", ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(1).plusHours(1), "DAILY", ZonedDateTime.now().plusDays(4));
		eventService.updateEvent(event, event_id);
		
		return "redirect:/calendar";
	}
}
