package com.demo.email.service;

import java.util.List;

public interface EmailSender {
    List<Long> sendAllPendingEmails();
}
