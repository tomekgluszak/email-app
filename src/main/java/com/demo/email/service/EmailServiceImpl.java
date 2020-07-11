package com.demo.email.service;

import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import com.demo.email.repository.EmailRepository;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String defaultSenderAddress;

    @Autowired
    private EmailRepository emailRepository;

    @Override
    public Email saveEmail(@NonNull Email email) {
        if (email.getSender() == null) {
            email.setSender(defaultSenderAddress);
        }
        return emailRepository.save(email);
    }

    @Override
    public Optional<Email> findById(@NonNull Long id) {
        return emailRepository.findById(id);
    }

    @Override
    public List<Email> findAllEmails() {
        return  Lists.newArrayList(emailRepository.findAll());
    }

    @Override
    public List<Email> findPendingEmails() {
        return emailRepository.findByStatus(EmailStatus.PENDING);
    }

    @Override
    public void updateStatusToSent(@NonNull Email email) {
        email.setStatus(EmailStatus.SENT);
        emailRepository.save(email);
    }
}
