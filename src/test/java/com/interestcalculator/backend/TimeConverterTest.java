package com.interestcalculator.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// A test is a small method with the label @Test. It runs your code with numbers
// where we already know the right answer, and checks that the code agrees.
//
// assertEquals(expected, actual, delta) means: "these two numbers must match,
// give or take a tiny difference (delta)". We allow a tiny difference because
// computers leave dust at the far end of decimals (like 7.000000000000001).
class TimeConverterTest {

    private static final double DELTA = 0.000001;

    @Test
    void yearsStayTheSame() {
        assertEquals(2.0, TimeConverter.toYears(2, 1), DELTA);
    }

    @Test
    void sixMonthsIsHalfAYear() {
        assertEquals(0.5, TimeConverter.toYears(6, 2), DELTA);
    }

    @Test
    void twentySixWeeksIsHalfAYear() {
        assertEquals(0.5, TimeConverter.toYears(26, 3), DELTA);
    }

    @Test
    void ninetyDaysInA360DayYearIsAQuarter() {
        assertEquals(0.25, TimeConverter.toYears(90, 4), DELTA);
    }

    @Test
    void daysInA365DayYear() {
        assertEquals(1.0, TimeConverter.toYears(365, 5), DELTA);
    }

    @Test
    void daysInA366DayYear() {
        assertEquals(0.5, TimeConverter.toYears(183, 6), DELTA);
    }

    @Test
    void unknownUnitGivesMinusOne() {
        assertEquals(-1.0, TimeConverter.toYears(5, 9), DELTA);
    }

    @Test
    void yearsBackToMonths() {
        assertEquals(6.0, TimeConverter.fromYears(0.5, 2), DELTA);
    }

    @Test
    void yearsBackToDays() {
        assertEquals(90.0, TimeConverter.fromYears(0.25, 4), DELTA);
    }
}
