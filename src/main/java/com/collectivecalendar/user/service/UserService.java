package com.collectivecalendar.user.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.collectivecalendar.model.User;
import com.collectivecalendar.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPassword())
				.roles("USER")
				.build();
	}

	public User getByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Transactional
	public void updateNotificationSettings(String username, boolean calendarNotify, boolean inviteNotify) {
		User user = getByUsername(username);
		user.setCalendarNotify(calendarNotify);
		user.setInviteNotify(inviteNotify);
		userRepository.save(user);
	}

	@Transactional
	public void changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
		User user = getByUsername(username);

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new IllegalArgumentException("Pašreizējā parole nav pareiza.");
		}

		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("Jaunās paroles nesakrīt.");
		}

		if (newPassword.length() < 8) {
			throw new IllegalArgumentException("Jaunajai parolei jābūt vismaz 8 rakstzīmēm.");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public void sendVerificationEmail(String username) {
		User user = getByUsername(username);
		// TODO: Integrate verification email dispatch service logic
		System.out.println("Verification email requested for user: " + user.getEmail());
	}
}
