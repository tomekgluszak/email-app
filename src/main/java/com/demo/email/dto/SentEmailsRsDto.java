package com.demo.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SentEmailsRsDto {
    private List<Long> sentEmailIds;
}
