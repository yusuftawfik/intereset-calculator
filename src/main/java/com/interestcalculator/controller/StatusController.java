package com.interestcalculator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController is a label meaning: "this class answers web requests".
@RestController
public class StatusController {

    // @GetMapping means: "when someone opens this address in a browser,
    // run the method below and send back whatever it returns".
    @GetMapping("/api/status")
    public String status() {
        return "Interest calculator is running";
    }
}
