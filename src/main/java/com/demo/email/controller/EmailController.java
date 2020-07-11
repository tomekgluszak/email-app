package com.demo.email.controller;

import com.demo.email.dto.EmailRqDto;
import com.demo.email.dto.EmailRsDto;
import com.demo.email.dto.SentEmailsRsDto;
import com.demo.email.exception.EmailNotFoundException;
import com.demo.email.mapper.EmailDtoMapper;
import com.demo.email.model.Email;
import com.demo.email.service.EmailSender;
import com.demo.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailDtoMapper emailDtoMapper;

    @Autowired
    private EmailSender emailSender;


    @GetMapping(path = "/emails", produces = "application/json")
    public ResponseEntity<List<EmailRsDto>> getEmails() {
        return ResponseEntity.status(HttpStatus.OK).body(emailDtoMapper.toRsDtos(emailService.findAllEmails()));
    }

    @GetMapping(path = "/emails/{id}", produces = "application/json")
    public ResponseEntity<EmailRsDto> getEmail(@PathVariable("id") Long id) {
        Email email = emailService.findById(id)
                .orElseThrow(() -> new EmailNotFoundException(id));
        return ResponseEntity.status(HttpStatus.OK).body(emailDtoMapper.toRsDto(email));
    }

    @PostMapping(path = "/emails", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EmailRsDto> createEmail(@RequestBody @Valid EmailRqDto emailRsDto) {
        Email newEmail = emailService.saveEmail(emailDtoMapper.toEntity(emailRsDto));
        EmailRsDto result = emailDtoMapper.toRsDto(newEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping(path = "/emails/send", produces = "application/json")
    public ResponseEntity<SentEmailsRsDto> sendAllPendingEmails() {
        List<Long> sentEmailIds = emailSender.sendAllPendingEmails();
        return ResponseEntity.status(HttpStatus.OK).body(new SentEmailsRsDto(sentEmailIds));
    }

}
