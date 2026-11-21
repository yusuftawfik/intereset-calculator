package com.interestcalculator.exception;

import java.util.List;

// The box we send back when something is wrong. It holds a list of
// plain messages, so the web page can show them all to the user.
public record ErrorResponse(List<String> errors) {
}
