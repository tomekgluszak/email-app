package com.demo.email.service;

import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import com.demo.email.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailServiceImpl underTest;

    @Mock
    private EmailRepository emailRepository;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(underTest, "defaultSenderAddress", "defaultSender@test.com");
    }
    
    @Test
    public void shouldSaveEmailTest() {
        Email emailToSave = createTestEmail();
        emailToSave.setSender("sender@test.com");

        Email createdEmail = createTestEmail();
        createdEmail.setId(11L);
        createdEmail.setSender("sender@test.com");
        createdEmail.setStatus(EmailStatus.PENDING);

        when(emailRepository.save(emailToSave)).thenReturn(createdEmail);


        Email result = underTest.saveEmail(emailToSave);

        verify(emailRepository).save(emailToSave);
        assertEquals(createdEmail, result);
    }

    @Test
    public void shouldPopulateDefaultSenderSaveEmailTest() {
        Email emailToSave = createTestEmail();

        Email createdEmail = createTestEmail();
        createdEmail.setId(11L);
        createdEmail.setSender("defaultSender@test.com");
        createdEmail.setStatus(EmailStatus.PENDING);

        when(emailRepository.save(emailToSave)).thenReturn(createdEmail);


        Email result = underTest.saveEmail(emailToSave);

        verify(emailRepository).save(emailToSave);
        assertEquals(createdEmail, result);
    }

    @Test
    public void shouldFindByIdTest() {
        long id = 11L;
        Email email = createTestEmail();
        email.setId(id);

        when(emailRepository.findById(id)).thenReturn(Optional.of(email));


        Optional<Email> result = underTest.findById(id);

        verify(emailRepository).findById(id);
        assertTrue(result.isPresent());
        assertEquals(email, result.get());
    }

    @Test
    public void shouldFindAllEmailsTest() {
        long id1 = 11L;
        Email email1 = createTestEmail();
        email1.setId(id1);

        long id2 = 12L;
        Email email2 = createTestEmail();
        email2.setId(id2);

        when(emailRepository.findAll()).thenReturn(Set.of(email1, email2));


        List<Email> result = underTest.findAllEmails();

        verify(emailRepository).findAll();
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(email1, email2));
    }

    @Test
    public void shouldFindPendingEmailsTest() {
        long id1 = 11L;
        Email email1 = createTestEmail();
        email1.setId(id1);
        email1.setStatus(EmailStatus.PENDING);

        long id2 = 12L;
        Email email2 = createTestEmail();
        email2.setId(id2);
        email2.setStatus(EmailStatus.PENDING);

        when(emailRepository.findByStatus(EmailStatus.PENDING)).thenReturn(List.of(email1, email2));


        List<Email> result = underTest.findPendingEmails();

        verify(emailRepository).findByStatus(EmailStatus.PENDING);
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(email1, email2));
    }

    @Test
    public void shouldUpdateStatusToSentTest() {
        Email email = createTestEmail();
        email.setStatus(EmailStatus.PENDING);


        underTest.updateStatusToSent(email);

        assertEquals(EmailStatus.SENT, email.getStatus());
        verify(emailRepository).save(email);
    }

    private Email createTestEmail() {
        return Email.builder()
                .recipients(Set.of("recipient1@test.com", "recipient2@test.com"))
                .subject("Message subject")
                .message("Test Email message")
                .build();
    }

}