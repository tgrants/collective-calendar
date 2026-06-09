package com.collectivecalendar;

import com.collectivecalendar.model.User;
import com.collectivecalendar.repository.UserRepository;
import com.collectivecalendar.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	@GetMapping("/login")
	public String login() {
		return "users/login";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		
		return "users/register";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, Model model) {
		user.setPassword(userService.encodePassword(user.getPassword()));
		userRepository.save(user);
		
		return "redirect:/login";
	}
}
