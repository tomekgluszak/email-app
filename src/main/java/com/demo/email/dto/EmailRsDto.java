package com.demo.email.dto;

import com.demo.email.model.EmailStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class EmailRsDto {

    private Long id;
    private String sender;
    private Set<String> recipients;
    private String subject;
    private String message;
    private EmailStatus status;

}
