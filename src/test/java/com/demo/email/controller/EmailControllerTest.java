package com.demo.email.controller;

import com.demo.email.dto.EmailRqDto;
import com.demo.email.exception.PendingEmailsNotFoundException;
import com.demo.email.mapper.EmailDtoMapper;
import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import com.demo.email.service.EmailSender;
import com.demo.email.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {

    private long id = 11l;
    private String recipient1 = "recipient1@test.com";
    private String recipient2 = "recipient2@test.com";
    private Set<String> recipients = Set.of(recipient1, recipient2);
    private String sender = "sender@test.com";
    private String subject = "test subject";
    private String message = "test email message";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmailDtoMapper emailDtoMapper;

    @MockBean
    private EmailSender emailSender;

    @Test
    public void shouldReturnEmail() throws Exception {
        Email email = Email.builder()
                .id(id)
                .sender(sender)
                .recipients(recipients)
                .subject(subject)
                .message(message)
                .build();

        when(emailService.findById(id)).thenReturn(Optional.of(email));
        when(emailDtoMapper.toRsDto(any())).thenCallRealMethod();

        mockMvc.perform(
                get("/emails/" + id)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.recipients").isArray())
                .andExpect(jsonPath("$.recipients", hasSize(2)))
                .andExpect(jsonPath("$.recipients", hasItem(recipient1)))
                .andExpect(jsonPath("$.recipients", hasItem(recipient2)))
                .andExpect(jsonPath("$.sender", is(sender)))
                .andExpect(jsonPath("$.subject", is(subject)))
                .andExpect(jsonPath("$.message", is(message)))
                .andExpect(jsonPath("$.status", is(EmailStatus.PENDING.toString())));
    }

    @Test
    public void shouldReturnEmails() throws Exception {
        Email email1 = Email.builder()
                .id(id)
                .sender(sender)
                .recipients(recipients)
                .subject(subject)
                .message(message)
                .build();

        long id2 = 12l;
        String recipient21 = "recipient21@test.com";
        String recipient22 = "recipient22@test.com";
        Set<String> recipients2 = Set.of(recipient1, recipient2);
        String sender2 = "sender2@test.com";
        String subject2 = "test subject2";
        String message2 = "test email message2";
        Email email2 = Email.builder()
                .id(id2)
                .sender(sender2)
                .recipients(recipients2)
                .subject(subject2)
                .message(message2)
                .build();

        when(emailService.findAllEmails()).thenReturn(List.of(email1, email2));
        when(emailDtoMapper.toRsDtos(any())).thenCallRealMethod();
        when(emailDtoMapper.toRsDto(any())).thenCallRealMethod();

        mockMvc.perform(
                get("/emails")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is((int) id)))
                .andExpect(jsonPath("$[0].recipients").isArray())
                .andExpect(jsonPath("$[0].recipients", hasSize(2)))
                .andExpect(jsonPath("$[0].recipients", hasItem(recipient1)))
                .andExpect(jsonPath("$[0].recipients", hasItem(recipient2)))
                .andExpect(jsonPath("$[0].sender", is(sender)))
                .andExpect(jsonPath("$[0].subject", is(subject)))
                .andExpect(jsonPath("$[0].message", is(message)))
                .andExpect(jsonPath("$[0].status", is(EmailStatus.PENDING.toString())))
                .andExpect(jsonPath("$[1].id", is((int) id2)))
                .andExpect(jsonPath("$[1].recipients").isArray())
                .andExpect(jsonPath("$[1].recipients", hasSize(2)))
                .andExpect(jsonPath("$[1].recipients", hasItem(recipient1)))
                .andExpect(jsonPath("$[1].recipients", hasItem(recipient2)))
                .andExpect(jsonPath("$[1].sender", is(sender2)))
                .andExpect(jsonPath("$[1].subject", is(subject2)))
                .andExpect(jsonPath("$[1].message", is(message2)))
                .andExpect(jsonPath("$[1].status", is(EmailStatus.PENDING.toString())));
    }

    @Test
    public void shouldReturn404WhenEmailNotFound() throws Exception {
        when(emailService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform
                (get("/emails/" + id)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn201WhenEmailCreated() throws Exception {
        Email email = Email.builder()
                .id(id)
                .sender(sender)
                .recipients(recipients)
                .subject(subject)
                .message(message)
                .build();

        EmailRqDto emailRqDto = EmailRqDto.builder()
                .sender(sender)
                .recipients(recipients)
                .subject(subject)
                .message(message)
                .build();

        when(emailDtoMapper.toEntity(emailRqDto)).thenCallRealMethod();
        when(emailService.saveEmail(any())).thenReturn(email);
        when(emailDtoMapper.toRsDto(any())).thenCallRealMethod();

        mockMvc.perform(
                post("/emails")
                        .content(objectMapper.writeValueAsString(emailRqDto))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.recipients").isArray())
                .andExpect(jsonPath("$.recipients", hasSize(2)))
                .andExpect(jsonPath("$.recipients", hasItem(recipient1)))
                .andExpect(jsonPath("$.recipients", hasItem(recipient2)))
                .andExpect(jsonPath("$.sender", is(sender)))
                .andExpect(jsonPath("$.subject", is(subject)))
                .andExpect(jsonPath("$.message", is(message)))
                .andExpect(jsonPath("$.status", is(EmailStatus.PENDING.toString())));

    }

    @Test
    public void shouldReturn400WhenEmptyMandatoryFields() throws Exception {
        String emailJsonData = "{\n" +
                "  \"sender\": \"" + sender + "\"\n" +
                "}";

        mockMvc.perform(
                post("/emails")
                        .content(emailJsonData)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message", hasSize(3)))
                .andExpect(jsonPath("$.message", hasItem("Recipient cannot be empty")))
                .andExpect(jsonPath("$.message", hasItem("Subject cannot be empty")))
                .andExpect(jsonPath("$.message", hasItem("Message cannot be empty")));
    }

    @Test
    public void shouldReturn400WhenNotValidEmailAddresses() throws Exception {
        EmailRqDto emailRqDto = EmailRqDto.builder()
                .sender("invalid@address@aaa.com")
                .recipients(Set.of(recipient1, "invalidaddress.com"))
                .subject(subject)
                .message(message)
                .build();

        mockMvc.perform(
                post("/emails")
                        .content(objectMapper.writeValueAsString(emailRqDto))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message").isArray())
                .andExpect(jsonPath("$.message", hasSize(2)))
                .andExpect(jsonPath("$.message", hasItem("Sender must be valid email address")))
                .andExpect(jsonPath("$.message", hasItem("Recipient must be valid email address")));
    }

    @Test
    public void shouldReturn200WhenEmailsSent() throws Exception {
        long id2 = 12l;
        when(emailSender.sendAllPendingEmails()).thenReturn(List.of(id, id2));

        mockMvc.perform(
                post("/emails/send")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sentEmailIds").isArray())
                .andExpect(jsonPath("$.sentEmailIds", hasSize(2)))
                .andExpect(jsonPath("$.sentEmailIds", hasItem((int) id)))
                .andExpect(jsonPath("$.sentEmailIds", hasItem((int) id2)));
    }

    @Test
    public void shouldReturn404WhenNoPendingEmailsToSend() throws Exception {
        when(emailSender.sendAllPendingEmails()).thenThrow(new PendingEmailsNotFoundException());

        mockMvc.perform(
                post("/emails/send")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404WhenWrongEndpoint() throws Exception {
        mockMvc.perform(
                get("/nonExistingEndpoint")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}