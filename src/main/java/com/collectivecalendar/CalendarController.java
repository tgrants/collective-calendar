package com.collectivecalendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.collectivecalendar.event.service.EventService;
import com.collectivecalendar.model.Event;
import com.collectivecalendar.model.Group;
import com.collectivecalendar.model.GroupEvent;
import com.collectivecalendar.model.User;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.EventRepository;
import com.collectivecalendar.repository.GroupEventRepository;
import com.collectivecalendar.repository.GroupRepository;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.repository.UserRepository;

@Controller
public class CalendarController {
	private final EventRepository eventRepository;
	private final GroupEventRepository groupEventsRepository;
	private final UserGroupRepository userGroupsRepository;
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final EventService eventService;

	public CalendarController(
		EventRepository eventRepository,
		GroupEventRepository groupEventsRepository,
		UserGroupRepository userGroupsRepository,
		UserRepository userRepository,
		GroupRepository groupRepository,
		EventService eventService
	) {
		this.eventRepository = eventRepository;
		this.groupEventsRepository = groupEventsRepository;
		this.userGroupsRepository = userGroupsRepository;
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
		this.eventService = eventService;
	}

	@GetMapping("/calendar")
	public String weekCalendar(
		@RequestParam(name = "weekOffset", required = false, defaultValue = "0") int weekOffset,
		@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
		Model model
	) {
		LocalDate today = LocalDate.now();
		LocalDate weekStart = today
				.plusWeeks(weekOffset)
				.with(DayOfWeek.MONDAY);

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.forLanguageTag("lv"));
		LocalDate weekEnd = weekStart.plusDays(6);
		String weekLabel = weekStart.format(fmt) + " – " + weekEnd.format(fmt);

		List<Map<String, Object>> weekDays = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			LocalDate d = weekStart.plusDays(i);

			Map<String, Object> day = new HashMap<>();
			day.put("date", d);
			day.put("dayNumber", d.getDayOfMonth());
			day.put("dayName", getLatvianShortDay(d.getDayOfWeek()));
			day.put("isToday", d.equals(today));

			weekDays.add(day);
		}

		List<Integer> hours = new ArrayList<>();
		for (int h = 7; h <= 22; h++) {
			hours.add(h);
		}

		User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
		List<UserGroup> userGroups = userGroupsRepository.findByUserId(user.getUid());

		Set<UUID> groupIds = userGroups.stream()
				.map(UserGroup::getGroupId)
				.collect(Collectors.toSet());

		Map<UUID, String> groupNames = groupRepository.findAllById(groupIds).stream()
				.collect(Collectors.toMap(Group::getUid, Group::getName));

		List<GroupEvent> groupEvents = groupEventsRepository.findByGroupIdIn(groupIds);
		Set<UUID> eventIds = groupEvents.stream()
				.map(GroupEvent::getEventId)
				.collect(Collectors.toSet());
		// List<Event> events = eventRepository.findAllById(eventIds);
		List<Event> events = eventRepository.findAll();

		List<Map<String, Object>> calendarEvents = new ArrayList<>();

		for (Event e : events) {
			List<LocalDateTime> eventInstances = eventService.getInstances(e, 100);
			LocalDateTime startTime = e.getStartTime();
			LocalDateTime endTime = e.getEndTime();

			long daysDuration    = ChronoUnit.DAYS.between(startTime, endTime);
			long hoursDuration   = ChronoUnit.HOURS.between(startTime, endTime) % 24;
			long minutesDuration = ChronoUnit.MINUTES.between(startTime, endTime) % 60;

			String groupName = groupEvents.stream()
					.filter(ge -> ge.getEventId().equals(e.getUid()))
					.map(ge -> groupNames.getOrDefault(ge.getGroupId(), "Grupa"))
					.findFirst()
					.orElse("Grupa");

			for (LocalDateTime instance : eventInstances) {
				LocalDateTime start = instance;
				LocalDateTime end = instance
						.plusDays(daysDuration)
						.plusHours(hoursDuration)
						.plusMinutes(minutesDuration);

				LocalDate eventDate = start.toLocalDate();
				int dayIndex = (int) Duration.between(weekStart.atStartOfDay(), eventDate.atStartOfDay()).toDays();

				if (dayIndex < 0 || dayIndex > 6) continue;

				int startMinutes = start.getHour() * 60 + start.getMinute();
				int endMinutes = end.getHour() * 60 + end.getMinute();

				int topPx = (startMinutes - 7 * 60) * 56 / 60;
				int heightPx = Math.max(20, (endMinutes - startMinutes) * 56 / 60);

				Map<String, Object> ev = new HashMap<>();
				ev.put("id", e.getUid());
				ev.put("name", e.getName());
				ev.put("dayIndex", dayIndex);
				ev.put("topPx", topPx);
				ev.put("heightPx", heightPx);
				ev.put("colorIndex", Math.abs(e.getUid().hashCode()) % 6);
				ev.put("groupName", groupName);

				calendarEvents.add(ev);
			}
		}

		List<String> groupColors = List.of(
				"#4f46e5", "#06b6d4", "#22c55e", "#f59e0b", "#ef4444", "#a855f7"
		);

		List<Map<String, Object>> userGroupsDisplay = userGroups.stream()
				.map(ug -> {
					Map<String, Object> m = new HashMap<>();
					m.put("groupId", ug.getGroupId());
					m.put("name", groupNames.getOrDefault(ug.getGroupId(), "—"));
					m.put("role", ug.getRole());
					return m;
				})
				.collect(Collectors.toList());

		model.addAttribute("weekLabel", weekLabel);
		model.addAttribute("weekDays", weekDays);
		model.addAttribute("hours", hours);
		model.addAttribute("calendarEvents", calendarEvents);
		model.addAttribute("weekOffset", weekOffset);
		model.addAttribute("userGroups", userGroupsDisplay);
		model.addAttribute("groupColors", groupColors);

		return "calendar/week";
	}

	private String getLatvianShortDay(DayOfWeek day) {
		return switch (day) {
			case MONDAY    -> "P";
			case TUESDAY   -> "O";
			case WEDNESDAY -> "T";
			case THURSDAY  -> "C";
			case FRIDAY    -> "Pk";
			case SATURDAY  -> "S";
			case SUNDAY    -> "Sv";
		};
	}
}
