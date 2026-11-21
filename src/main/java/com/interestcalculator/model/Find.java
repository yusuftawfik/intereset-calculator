package com.interestcalculator.model;

// A fixed list of allowed choices: what does the user want to find?
// AMOUNT    = the final amount (S), plus the interest (I)
// PRINCIPAL = the starting money (P)
// RATE      = the yearly rate in percent
// TIME      = the time, in years
public enum Find {
    AMOUNT,
    PRINCIPAL,
    RATE,
    TIME
}
