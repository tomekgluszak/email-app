package com.demo.email.service;

import com.demo.email.model.Email;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface EmailService {
    Email saveEmail(@NonNull Email email);

    Optional<Email> findById(@NonNull Long id);

    List<Email> findAllEmails();

    List<Email> findPendingEmails();

    void updateStatusToSent(@NonNull Email email);
}
