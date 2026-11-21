package com.interestcalculator.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Every number here comes from your slides (or can be checked by hand).
class SimpleInterestTest {

    private static final double DELTA = 0.01;

    // ---------- Slide example 1: P = 8000, rate = 7%, time = 2 years ----------
    @Test
    void interestForSlideExampleOne() {
        assertEquals(1120.0, SimpleInterest.interest(8000, 7, 2), DELTA);
    }

    @Test
    void amountForSlideExampleOne() {
        assertEquals(9120.0, SimpleInterest.amount(8000, 7, 2), DELTA);
    }

    // ---------- Slide example 3: P = 10000, rate = 10%, 154 days ----------
    @Test
    void interestWhenTimeIsGivenInDays() {
        double years = TimeConverter.toYears(154, 4);
        assertEquals(427.78, SimpleInterest.interest(10000, 10, years), DELTA);
    }

    // ---------- Working backwards: each one should give back the original numbers ----------
    @Test
    void principalFromInterest() {
        assertEquals(8000.0, SimpleInterest.principalFromInterest(1120, 7, 2), DELTA);
    }

    @Test
    void principalFromAmount() {
        assertEquals(8000.0, SimpleInterest.principalFromAmount(9120, 7, 2), DELTA);
    }

    @Test
    void rateFromInterest() {
        assertEquals(7.0, SimpleInterest.rateFromInterest(1120, 8000, 2), DELTA);
    }

    @Test
    void rateFromAmount() {
        assertEquals(7.0, SimpleInterest.rateFromAmount(9120, 8000, 2), DELTA);
    }

    @Test
    void timeFromInterest() {
        assertEquals(2.0, SimpleInterest.timeFromInterest(1120, 8000, 7), DELTA);
    }

    @Test
    void timeFromAmount() {
        assertEquals(2.0, SimpleInterest.timeFromAmount(9120, 8000, 7), DELTA);
    }
}
