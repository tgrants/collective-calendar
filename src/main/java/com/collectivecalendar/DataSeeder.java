package com.collectivecalendar;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final EventRepository eventRepository;
	private final GroupEventRepository groupEventRepository;
	private final UserGroupRepository userGroupRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {
		// Seed admin user
		createAdminUser();
		
		// Seed notification test
		User stressUser = createStressUserIfNotExists();
		Group stressGroup = createStressGroupIfNotExists(stressUser.getUid());
		linkUserToGroup(stressUser, stressGroup);
		seedHighDensityEvents(stressGroup);
	}

	private void createAdminUser() {
		if (userRepository.findByUsername("admin").isEmpty()) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setEmail("admin@local.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setVerified(true);
			admin.setCalendarNotify(true);
			admin.setInviteNotify(true);
			userRepository.save(admin);
			System.out.println("Seeded clean admin user: admin / admin123");
		}
	}

	private User createStressUserIfNotExists() {
		return userRepository.findByUsername("user1").orElseGet(() -> {
			User user = new User();
			user.setUsername("user1");
			user.setEmail("user1@local.com");
			user.setPassword(passwordEncoder.encode("user1234"));
			user.setVerified(true);
			user.setCalendarNotify(true);
			user.setInviteNotify(false);
			System.out.println("Seeded dedicated stress testing user: user1 / user1234");
			return userRepository.save(user);
		});
	}

	private Group createStressGroupIfNotExists(UUID stressUserId) {
		return userGroupRepository.findAll().stream()
			.filter(ug -> ug.getUserId().equals(stressUserId))
			.map(ug -> groupRepository.findById(ug.getGroupId()).orElse(null))
			.filter(g -> g != null && "Stress Test Group".equals(g.getName()))
			.findFirst()
			.orElseGet(() -> {
				Group group = new Group();
				group.setName("Stress Test Group");
				System.out.println("Created clean Stress Test Group exclusively for user1.");
				return groupRepository.save(group);
			});
	}

	private void linkUserToGroup(User user, Group group) {
		boolean linkExists = userGroupRepository.findAll().stream()
			.anyMatch(ug -> ug.getGroupId().equals(group.getUid()) && ug.getUserId().equals(user.getUid()));

		if (!linkExists) {
			UserGroup userGroup = new UserGroup();
			userGroup.setUserId(user.getUid());
			userGroup.setGroupId(group.getUid());
			userGroup.setRole("EDITOR");
			userGroup.setNotify(true);
			userGroupRepository.save(userGroup);
		}
	}

	private void seedHighDensityEvents(Group group) {
		long existingStressEvents = groupEventRepository.findByGroupId(group.getUid()).size();
		if (existingStressEvents > 0) {
			System.out.println("Stress test data already exists. Skipping high-density generation.");
			return;
		}

		System.out.println("Starting high-density event generation (1,440 minute-interval events)...");

		LocalDateTime baseTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

		for (int i = 0; i < 1440; i++) {
			LocalDateTime eventStart = baseTime.plusMinutes(i);
			
			Event event = new Event();
			event.setName("Load Test Event #" + i);
			event.setStartTime(eventStart);
			event.setEndTime(eventStart.plusMinutes(15));
			event.setFrequency("MINUTELY"); 
			event = eventRepository.save(event);

			GroupEvent groupEvent = new GroupEvent();
			groupEvent.setGroupId(group.getUid());
			groupEvent.setEventId(event.getUid());
			groupEventRepository.save(groupEvent);
		}

		System.out.println("Successfully generated 1,440 events isolated to the stress test group.");
	}
}
