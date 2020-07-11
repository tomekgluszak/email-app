package com.demo.email.model;

import com.demo.email.converter.SetToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    @Convert(converter = SetToStringConverter.class)
    private Set<String> recipients;
    private String subject;
    private String message;
    @Builder.Default
    private EmailStatus status = EmailStatus.PENDING;

}
