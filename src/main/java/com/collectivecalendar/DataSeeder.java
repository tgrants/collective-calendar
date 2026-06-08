package com.collectivecalendar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.collectivecalendar.model.User;
import com.collectivecalendar.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		createAdminIfNotExists();
	}

	private void createAdminIfNotExists() {
		boolean adminExists = userRepository.findByUsername("admin").isPresent();

		if (adminExists) {
			System.out.println("Admin already exists, skipping seed.");
			return;
		}

		User admin = new User();
		admin.setUsername("admin");
		admin.setEmail("admin@local.com");
		admin.setPassword(passwordEncoder.encode("admin123"));
		admin.setVerified(true);
		admin.setCalendarNotify(true);
		admin.setInviteNotify(true);

		userRepository.save(admin);

		System.out.println("Admin created: admin / admin123");
	}
}
