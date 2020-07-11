package com.demo.email.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
public class EmailRqDto {

    @Nullable
    @Email(message="Sender must be valid email address")
    private String sender;
    @NotEmpty(message = "Recipient cannot be empty")
    private Set<@Email(message="Recipient must be valid email address") String> recipients;
    @NotEmpty(message = "Subject cannot be empty")
    private String subject;
    @NotEmpty(message = "Message cannot be empty")
    private String message;

}
