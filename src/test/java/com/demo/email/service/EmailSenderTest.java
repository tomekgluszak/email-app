package com.demo.email.service;

import com.demo.email.exception.PendingEmailsNotFoundException;
import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class EmailSenderTest {

    @InjectMocks
    private EmailSenderImpl underTest;

    @Mock
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void shouldSendAllPendingEmailsTest() {
        long id1 = 1L;
        Email email1 = createTestEmail(id1);
        long id2 = 2L;
        Email email2 = createTestEmail(id2);
        when(emailService.findPendingEmails()).thenReturn(List.of(email1, email2));

        List<Long> result = underTest.sendAllPendingEmails();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(id1, id2));
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
        verify(emailService).updateStatusToSent(email1);
        verify(emailService).updateStatusToSent(email2);
    }

    @Test()
    void shouldThrowWhenNoPendingEmailsTest() {
        when(emailService.findPendingEmails()).thenReturn(Collections.emptyList());

        assertThrows(PendingEmailsNotFoundException.class, () -> underTest.sendAllPendingEmails());
    }

    @Test
    void shouldNotReturnIdWhenEmailNotSentTest() {
        long id1 = 1L;
        Email email1 = createTestEmail(id1);
        long id2 = 2L;
        Email email2 = createTestEmail(id2);
        email2.setSender("different subject");
        when(emailService.findPendingEmails()).thenReturn(List.of(email1, email2));
        doThrow(new MailSendException("")).when(javaMailSender).send(underTest.mapToSimpleMailMessage(email1));

        List<Long> result = underTest.sendAllPendingEmails();

        assertThat(result, hasSize(1));
        assertThat(result, containsInAnyOrder(id2));
        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));
        verify(emailService).updateStatusToSent(email2);
    }

    private Email createTestEmail(Long id) {
        return Email.builder()
                .id(id)
                .sender("sender@test.com")
                .recipients(Set.of("recipient1@test.com", "recipient2@test.com"))
                .subject("Message subject")
                .message("Test Email message")
                .status(EmailStatus.PENDING)
                .build();
    }

}