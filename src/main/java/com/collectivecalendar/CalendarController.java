package com.collectivecalendar;

import com.collectivecalendar.model.Event;
import com.collectivecalendar.repository.EventRepository;
import com.collectivecalendar.model.GroupEvent;
import com.collectivecalendar.model.User;
import com.collectivecalendar.repository.GroupEventRepository;
import com.collectivecalendar.repository.UserRepository;
import com.collectivecalendar.model.UserGroup;
import com.collectivecalendar.repository.UserGroupRepository;
import com.collectivecalendar.event.service.EventService;
import com.collectivecalendar.event.service.EventServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CalendarController {
    private final EventRepository eventRepository;
    private final GroupEventRepository groupEventsRepository;
    private final UserGroupRepository userGroupsRepository;
    private final UserRepository userRepository;

    public CalendarController(
        EventRepository eventRepository,
        GroupEventRepository groupEventsRepository,
        UserGroupRepository userGroupsRepository,
        UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.groupEventsRepository = groupEventsRepository;
        this.userGroupsRepository = userGroupsRepository;
        this.userRepository = userRepository;
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
        List<GroupEvent> groupEvents = groupEventsRepository.findByGroupIdIn(groupIds);
        Set<UUID> eventIds = groupEvents.stream()
                .map(GroupEvent::getEventId)
                .collect(Collectors.toSet());
        List<Event> events = eventRepository.findAllById(eventIds);

        List<Map<String, Object>> calendarEvents = new ArrayList<>();

        EventService eventService = new EventServiceImpl();
        for (Event e : events) {
        	List<ZonedDateTime> eventInstances = eventService.getAllInstances(e);
        	LocalDateTime startTime = e.getStartTime().toLocalDateTime();
        	LocalDateTime endTime = e.getEndTime().toLocalDateTime();
        	
        	long daysDuration = ChronoUnit.MONTHS.between(startTime, endTime);
        	long hoursDuration = ChronoUnit.MONTHS.between(startTime, endTime);
        	long minutesDuration = ChronoUnit.MONTHS.between(startTime, endTime);
        	
        	for (ZonedDateTime instance : eventInstances) {
        		LocalDateTime start = instance.toLocalDateTime();
                LocalDateTime end = instance.plusDays(daysDuration).plusHours(hoursDuration).plusMinutes(minutesDuration).toLocalDateTime();

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
                ev.put("groupName", "Grupa");

                calendarEvents.add(ev);
        	}
        }

        List<String> groupColors = List.of(
                "#4f46e5", "#06b6d4", "#22c55e", "#f59e0b", "#ef4444", "#a855f7"
        );

        model.addAttribute("weekLabel", weekLabel);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("hours", hours);
        model.addAttribute("calendarEvents", calendarEvents);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("userGroups", userGroups);
        model.addAttribute("groupColors", groupColors);

        return "calendar/week";
    }

    private String getLatvianShortDay(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "P";
            case TUESDAY -> "O";
            case WEDNESDAY -> "T";
            case THURSDAY -> "C";
            case FRIDAY -> "Pk";
            case SATURDAY -> "S";
            case SUNDAY -> "Sv";
        };
    }
}