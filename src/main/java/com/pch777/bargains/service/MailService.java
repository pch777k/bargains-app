package com.pch777.bargains.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MailService {
	
	private JavaMailSender javaMailSender;
	
	public void sendEmail(String from, String to, String text, String subject) {
	SimpleMailMessage message = new SimpleMailMessage();
	
	message.setFrom(from);
	message.setTo(to);
	message.setText(text);
	message.setSubject(subject);
	
	javaMailSender.send(message);
	}

}
