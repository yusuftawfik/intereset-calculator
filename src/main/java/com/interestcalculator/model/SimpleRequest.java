package com.interestcalculator.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// A "record" is a small box that holds values. This box holds everything the web page
// sends us for a SIMPLE interest calculation. Fields the user doesn't know can be left out.
//
// The labels like @Positive are rules. If a value breaks its rule, the app refuses
// the request and sends back the message written inside the label.
//
// find      = what to find (AMOUNT, PRINCIPAL, RATE or TIME)
// known     = for PRINCIPAL, RATE or TIME: do they know the INTEREST or the AMOUNT?
// principal = starting money (P)
// rate      = yearly rate in percent (7 means 7%)
// interest  = interest (I)
// amount    = final amount (S)
// time      = amount of time, in the unit below
// unit      = 1 years, 2 months, 3 weeks, 4 days (360), 5 days (365), 6 days (366)
public record SimpleRequest(
        @NotNull(message = "Choose what to find: AMOUNT, PRINCIPAL, RATE or TIME")
        Find find,

        Known known,

        @Positive(message = "Principal must be greater than 0")
        Double principal,

        @Positive(message = "Rate must be greater than 0")
        Double rate,

        @Positive(message = "Interest must be greater than 0")
        Double interest,

        @Positive(message = "Amount must be greater than 0")
        Double amount,

        @Positive(message = "Time must be greater than 0")
        Double time,

        @Min(value = 1, message = "Time unit must be a number from 1 to 6")
        @Max(value = 6, message = "Time unit must be a number from 1 to 6")
        Integer unit
) {
}
