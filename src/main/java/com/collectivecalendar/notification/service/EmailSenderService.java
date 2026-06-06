package com.collectivecalendar.notification.service;

public interface EmailSenderService {
	/**
	 * Dispatches an HTML-formatted email message.
	 * * @param to          The recipient's email address.
	 * @param subject     The subject line of the email.
	 * @param htmlContent The HTML message body.
	 * @return true if the email was successfully accepted by the SMTP server, 
	 * false if an exception occurred.
	 */
	boolean sendHtmlEmail(String to, String subject, String htmlContent);
}
