package com.karacam.bookie.core.utils;

import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtility {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailUtility(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SneakyThrows
    public void sendHtmlEmail(String to, String from, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom(from);

        mailSender.send(message);
    }
}
