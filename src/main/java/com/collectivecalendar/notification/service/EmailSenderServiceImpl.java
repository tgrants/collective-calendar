package com.collectivecalendar.notification.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

	private final JavaMailSender mailSender;

	@Override
	public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			helper.setFrom("noreply@collectivecalendar.local");

			mailSender.send(message);
			log.info("Successfully dispatched email alert to target recipient: {}", to);
			return true;

		} catch (Exception ex) {
			log.error("Failed to transmit email payload to recipient: " + to + " due to unexpected transport failure", ex);
			return false;
		}
	}
}
