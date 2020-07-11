package com.demo.email.mapper;

import com.demo.email.dto.EmailRqDto;
import com.demo.email.dto.EmailRsDto;
import com.demo.email.model.Email;
import com.demo.email.model.EmailStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class EmailDtoMapperTest {

    private EmailDtoMapper underTest = new EmailDtoMapper();

    private String sender = "sender@test.com";
    private String recipient1 = "recipient1@test.com";
    private String recipient2 = "recipient2@test.com";
    private String subject = "Test subject";
    private String message = "Test message";

    @Test
    public void shouldMapToEntityTest() {

        EmailRqDto input = EmailRqDto.builder()
                .sender(sender)
                .recipients(Set.of(recipient1, recipient2))
                .subject(subject)
                .message(message)
                .build();

        Email result = underTest.toEntity(input);

        assertEquals(sender, result.getSender());
        assertThat(result.getRecipients(), hasSize(2));
        assertThat(result.getRecipients(), containsInAnyOrder(recipient1, recipient2));
        assertEquals(subject, result.getSubject());
        assertEquals(message, result.getMessage());
        assertEquals(EmailStatus.PENDING, result.getStatus());
    }

    @Test
    public void shouldMapToRsDtoTest() {
        long id = 3L;
        Email input = Email.builder()
                .id(id)
                .sender(sender)
                .recipients(Set.of(recipient1, recipient2))
                .subject(subject)
                .message(message)
                .status(EmailStatus.SENT)
                .build();

        EmailRsDto result = underTest.toRsDto(input);

        assertEquals(id, result.getId());
        assertEquals(sender, result.getSender());
        assertThat(result.getRecipients(), hasSize(2));
        assertThat(result.getRecipients(), containsInAnyOrder(recipient1, recipient2));
        assertEquals(subject, result.getSubject());
        assertEquals(message, result.getMessage());
        assertEquals(EmailStatus.SENT, result.getStatus());
    }

    @Test
    public void shouldMapToRsDtosTest() {
        long id = 3L;
        Email input = Email.builder()
                .id(id)
                .sender(sender)
                .recipients(Set.of(recipient1, recipient2))
                .subject(subject)
                .message(message)
                .status(EmailStatus.SENT)
                .build();

        List<EmailRsDto> result = underTest.toRsDtos(List.of(input));

        assertThat(result, hasSize(1));
        assertEquals(id, result.get(0).getId());
        assertEquals(sender, result.get(0).getSender());
        assertThat(result.get(0).getRecipients(), hasSize(2));
        assertThat(result.get(0).getRecipients(), containsInAnyOrder(recipient1, recipient2));
        assertEquals(subject, result.get(0).getSubject());
        assertEquals(message, result.get(0).getMessage());
        assertEquals(EmailStatus.SENT, result.get(0).getStatus());

    }
}