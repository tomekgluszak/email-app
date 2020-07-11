package com.demo.email.repository;

import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmailRepository extends CrudRepository<Email, Long> {
    List<Email> findByStatus(EmailStatus emailStatus);
}
