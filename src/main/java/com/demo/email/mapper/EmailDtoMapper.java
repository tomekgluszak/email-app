package com.demo.email.mapper;

import com.demo.email.dto.EmailRqDto;
import com.demo.email.dto.EmailRsDto;
import com.demo.email.model.Email;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailDtoMapper {

    public Email toEntity(EmailRqDto emailRqDto) {
        Email email = Email.builder()
                .subject(emailRqDto.getSubject())
                .message(emailRqDto.getMessage())
                .sender(emailRqDto.getSender())
                .recipients(emailRqDto.getRecipients())
                .build();
        return email;
    }

    public EmailRsDto toRsDto(Email email) {
        if (email == null) {
            return null;
        }
        EmailRsDto emailRsDto = EmailRsDto.builder()
                .id(email.getId())
                .subject(email.getSubject())
                .message(email.getMessage())
                .sender(email.getSender())
                .recipients(email.getRecipients())
                .status(email.getStatus())
                .build();
        return emailRsDto;
    }

    public List<EmailRsDto> toRsDtos(Collection<Email> emails) {
        return emails.stream()
                .map(this::toRsDto)
                .collect(Collectors.toList());
    }

}
