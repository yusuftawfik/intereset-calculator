package com.interestcalculator.service;

import com.interestcalculator.exception.InvalidRequestException;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.Find;
import com.interestcalculator.model.Known;
import com.interestcalculator.model.SimpleRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// These tests check the whole simple-interest service: a request goes in,
// and the full answer (all five values) must come out right.
class SimpleInterestServiceTest {

    private static final double DELTA = 0.01;

    private final SimpleInterestService service = new SimpleInterestService();

    // SimpleRequest(find, known, principal, rate, interest, amount, time, unit)

    @Test
    void findAmountGivesInterestAndAmount() {
        SimpleRequest request = new SimpleRequest(Find.AMOUNT, null, 8000.0, 7.0, null, null, 2.0, 1);

        CalculationResult result = service.calculate(request);

        assertEquals(1120.0, result.interest(), DELTA);
        assertEquals(9120.0, result.amount(), DELTA);
        assertEquals(2.0, result.timeInYears(), DELTA);
    }

    @Test
    void timeInDaysIsConvertedToYears() {
        SimpleRequest request = new SimpleRequest(Find.AMOUNT, null, 10000.0, 10.0, null, null, 154.0, 4);

        CalculationResult result = service.calculate(request);

        assertEquals(427.78, result.interest(), DELTA);
        assertEquals(154.0 / 360, result.timeInYears(), DELTA);
    }

    @Test
    void findPrincipalFromInterest() {
        SimpleRequest request = new SimpleRequest(Find.PRINCIPAL, Known.INTEREST, null, 7.0, 1120.0, null, 2.0, 1);

        assertEquals(8000.0, service.calculate(request).principal(), DELTA);
    }

    @Test
    void findRateFromAmount() {
        SimpleRequest request = new SimpleRequest(Find.RATE, Known.AMOUNT, 8000.0, null, null, 9120.0, 2.0, 1);

        assertEquals(7.0, service.calculate(request).rate(), DELTA);
    }

    @Test
    void findTimeFromInterest() {
        SimpleRequest request = new SimpleRequest(Find.TIME, Known.INTEREST, 8000.0, 7.0, 1120.0, null, null, null);

        assertEquals(2.0, service.calculate(request).timeInYears(), DELTA);
    }

    @Test
    void missingRateIsRefused() {
        SimpleRequest request = new SimpleRequest(Find.AMOUNT, null, 8000.0, null, null, null, 2.0, 1);

        assertThrows(InvalidRequestException.class, () -> service.calculate(request));
    }
}
