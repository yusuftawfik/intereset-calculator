package com.interestcalculator.exception;

import java.util.List;

// Our own kind of "problem". When the request is wrong in some way,
// the code "throws" one of these, which stops the calculation and carries the messages.
public class InvalidRequestException extends RuntimeException {

    private final List<String> errors;

    public InvalidRequestException(List<String> errors) {
        super("Invalid request");
        this.errors = errors;
    }

    public InvalidRequestException(String error) {
        this(List.of(error));
    }

    public List<String> getErrors() {
        return errors;
    }
}
