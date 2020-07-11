package com.demo.email.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PendingEmailsNotFoundException extends RuntimeException {
    public PendingEmailsNotFoundException() {
        super("No pending emails has been found");
    }
}
