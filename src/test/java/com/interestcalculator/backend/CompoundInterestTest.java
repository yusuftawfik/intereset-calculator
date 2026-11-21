package com.interestcalculator.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompoundInterestTest {

    private static final double DELTA = 0.01;

    // P = 10000, rate = 10%, added quarterly, half a year:
    // 2 periods of 2.5% each -> 10000 * 1.025 * 1.025 = 10506.25
    @Test
    void amountAddedQuarterlyForHalfAYear() {
        assertEquals(10506.25, CompoundInterest.amount(10000, 10, 4, 0.5), DELTA);
    }

    @Test
    void interestAddedQuarterlyForHalfAYear() {
        assertEquals(506.25, CompoundInterest.interest(10000, 10, 4, 0.5), DELTA);
    }

    @Test
    void amountAddedQuarterlyForTwoYears() {
        assertEquals(1171.66, CompoundInterest.amount(1000, 8, 4, 2), DELTA);
    }

    // ---------- Working backwards ----------
    @Test
    void principalFromAmount() {
        assertEquals(10000.0, CompoundInterest.principal(10506.25, 10, 4, 0.5), DELTA);
    }

    @Test
    void rateFromAmount() {
        assertEquals(10.0, CompoundInterest.rate(10506.25, 10000, 4, 0.5), DELTA);
    }

    @Test
    void timeFromAmount() {
        assertEquals(0.5, CompoundInterest.time(10506.25, 10000, 10, 4), DELTA);
    }
}
