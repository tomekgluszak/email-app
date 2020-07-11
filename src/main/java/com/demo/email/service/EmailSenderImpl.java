package com.demo.email.service;

import com.demo.email.exception.PendingEmailsNotFoundException;
import com.demo.email.model.Email;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class EmailSenderImpl implements EmailSender {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public List<Long> sendAllPendingEmails() {
        List<Email> emails = emailService.findPendingEmails();
        if (emails.isEmpty()) {
            throw new PendingEmailsNotFoundException();
        }
        List<Long> sentEmailIds = new ArrayList<>();
        for (Email email : emails) {
            try {
                sendEmail(email);
                emailService.updateStatusToSent(email);
                sentEmailIds.add(email.getId());
            } catch (MailException e) {
                log.error(e);
            }
        }
        return sentEmailIds;
    }

    private void sendEmail(Email email) {
        SimpleMailMessage simpleMailMessage = mapToSimpleMailMessage(email);
        javaMailSender.send(simpleMailMessage);
    }

    SimpleMailMessage mapToSimpleMailMessage(Email email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(email.getSender());
        simpleMailMessage.setTo(email.getRecipients().stream().toArray(String[]::new));
        simpleMailMessage.setSubject(email.getSubject());
        simpleMailMessage.setText(email.getMessage());
        return simpleMailMessage;
    }
}
