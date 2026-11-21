package com.interestcalculator.service;

import com.interestcalculator.exception.InvalidRequestException;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.CompoundRequest;
import com.interestcalculator.model.Find;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompoundInterestServiceTest {

    private static final double DELTA = 0.01;

    private final CompoundInterestService service = new CompoundInterestService();

    // CompoundRequest(find, principal, rate, amount, time, unit, timesPerYear)

    @Test
    void findAmountQuarterlyForOneHundredEightyDays() {
        CompoundRequest request = new CompoundRequest(Find.AMOUNT, 10000.0, 10.0, null, 180.0, 4, 4);

        CalculationResult result = service.calculate(request);

        assertEquals(10506.25, result.amount(), DELTA);
        assertEquals(506.25, result.interest(), DELTA);
        assertEquals(0.5, result.timeInYears(), DELTA);
    }

    @Test
    void findPrincipal() {
        CompoundRequest request = new CompoundRequest(Find.PRINCIPAL, null, 10.0, 10506.25, 0.5, 1, 4);

        assertEquals(10000.0, service.calculate(request).principal(), DELTA);
    }

    @Test
    void findRate() {
        CompoundRequest request = new CompoundRequest(Find.RATE, 10000.0, null, 10506.25, 0.5, 1, 4);

        assertEquals(10.0, service.calculate(request).rate(), DELTA);
    }

    @Test
    void findTime() {
        CompoundRequest request = new CompoundRequest(Find.TIME, 10000.0, 10.0, 10506.25, null, null, 4);

        assertEquals(0.5, service.calculate(request).timeInYears(), DELTA);
    }

    @Test
    void missingTimesPerYearIsRefused() {
        CompoundRequest request = new CompoundRequest(Find.AMOUNT, 10000.0, 10.0, null, 2.0, 1, null);

        assertThrows(InvalidRequestException.class, () -> service.calculate(request));
    }
}
