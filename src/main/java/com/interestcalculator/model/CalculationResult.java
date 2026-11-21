package com.interestcalculator.model;

// The box we send back to the web page. It always holds the full picture,
// so the page can show all five values whatever the user asked for.
//
// timeInYears = the time converted to years
public record CalculationResult(
        double principal,
        double interest,
        double amount,
        double rate,
        double timeInYears
) {
}
