package com.interestcalculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

// This class catches problems from anywhere in the app and turns each one into a
// tidy reply. Status 400 means "the request you sent has a problem" (not our fault).
// @RestControllerAdvice is the label that says "watch every controller for problems".
@RestControllerAdvice
public class ApiExceptionHandler {

    // Our own rules (missing field, amount smaller than principal, result too big...)
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequest(InvalidRequestException e) {
        return new ErrorResponse(e.getErrors());
    }

    // The labels on the request boxes (like @Positive) were broken
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBrokenLabels(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        return new ErrorResponse(errors);
    }

    // The request could not even be read (broken JSON, text where a number should be,
    // a "find" value that is not in the allowed list...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadable(HttpMessageNotReadableException e) {
        return new ErrorResponse(List.of(
                "The request could not be read. Check that every value is a number "
                        + "and that find is AMOUNT, PRINCIPAL, RATE or TIME"));
    }
}
