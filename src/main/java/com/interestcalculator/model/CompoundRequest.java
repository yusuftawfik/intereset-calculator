package com.interestcalculator.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// The box for a COMPOUND interest calculation.
// Compound interest always uses the amount (S), never the interest, so there is no "known".
//
// timesPerYear = how many times a year interest is added
//                1 yearly, 2 half-yearly, 4 quarterly, 12 monthly, 52 weekly, 360 daily
public record CompoundRequest(
        @NotNull(message = "Choose what to find: AMOUNT, PRINCIPAL, RATE or TIME")
        Find find,

        @Positive(message = "Principal must be greater than 0")
        Double principal,

        @Positive(message = "Rate must be greater than 0")
        Double rate,

        @Positive(message = "Amount must be greater than 0")
        Double amount,

        @Positive(message = "Time must be greater than 0")
        Double time,

        @Min(value = 1, message = "Time unit must be a number from 1 to 6")
        @Max(value = 6, message = "Time unit must be a number from 1 to 6")
        Integer unit,

        Integer timesPerYear
) {
}
