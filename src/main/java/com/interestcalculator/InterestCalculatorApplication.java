package com.interestcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is where the web app starts.
// @SpringBootApplication is a label that tells Spring: "start here, and look inside
// this folder (and everything below it) for the other pieces of the app".
@SpringBootApplication
public class InterestCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterestCalculatorApplication.class, args);
    }
}
