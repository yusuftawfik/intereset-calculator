package com.interestcalculator.service;

import com.interestcalculator.exception.InvalidRequestException;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.CompoundRequest;
import com.interestcalculator.model.Find;
import com.interestcalculator.model.Known;
import com.interestcalculator.model.SimpleRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// These tests check the rules in RequestChecks.
// assertThrows means: "this code MUST fail with this kind of problem, or the test fails".
class RequestChecksTest {

    // The request boxes hold: SimpleRequest(find, known, principal, rate, interest, amount, time, unit)

    @Test
    void findingAmountWithNothingElseListsEverythingMissing() {
        SimpleRequest request = new SimpleRequest(Find.AMOUNT, null, null, null, null, null, null, null);

        InvalidRequestException problem = assertThrows(InvalidRequestException.class,
                () -> RequestChecks.checkSimple(request));

        assertTrue(problem.getErrors().contains("Please enter the principal"));
        assertTrue(problem.getErrors().contains("Please enter the rate"));
        assertTrue(problem.getErrors().contains("Please enter the time"));
        assertTrue(problem.getErrors().contains("Please enter the time unit"));
    }

    @Test
    void findingRateWithoutSayingWhatYouKnowIsRefused() {
        SimpleRequest request = new SimpleRequest(Find.RATE, null, 8000.0, null, null, null, 2.0, 1);

        InvalidRequestException problem = assertThrows(InvalidRequestException.class,
                () -> RequestChecks.checkSimple(request));

        assertTrue(problem.getErrors().contains(
                "Please say what you know: INTEREST or AMOUNT (the known field)"));
    }

    @Test
    void amountSmallerThanPrincipalIsRefused() {
        SimpleRequest request = new SimpleRequest(Find.RATE, Known.AMOUNT, 8000.0, null, null, 7000.0, 2.0, 1);

        InvalidRequestException problem = assertThrows(InvalidRequestException.class,
                () -> RequestChecks.checkSimple(request));

        assertTrue(problem.getErrors().contains("The amount must be greater than the principal"));
    }

    @Test
    void aCompleteSimpleRequestPasses() {
        SimpleRequest request = new SimpleRequest(Find.AMOUNT, null, 8000.0, 7.0, null, null, 2.0, 1);

        assertDoesNotThrow(() -> RequestChecks.checkSimple(request));
    }

    // CompoundRequest(find, principal, rate, amount, time, unit, timesPerYear)

    @Test
    void unusualTimesPerYearIsRefused() {
        CompoundRequest request = new CompoundRequest(Find.AMOUNT, 10000.0, 10.0, null, 2.0, 1, 5);

        InvalidRequestException problem = assertThrows(InvalidRequestException.class,
                () -> RequestChecks.checkCompound(request));

        assertTrue(problem.getErrors().contains("timesPerYear must be 1, 2, 4, 12, 52 or 360"));
    }

    @Test
    void missingTimesPerYearIsRefused() {
        CompoundRequest request = new CompoundRequest(Find.AMOUNT, 10000.0, 10.0, null, 2.0, 1, null);

        assertThrows(InvalidRequestException.class, () -> RequestChecks.checkCompound(request));
    }

    @Test
    void aCompleteCompoundRequestPasses() {
        CompoundRequest request = new CompoundRequest(Find.AMOUNT, 10000.0, 10.0, null, 2.0, 1, 4);

        assertDoesNotThrow(() -> RequestChecks.checkCompound(request));
    }

    // CalculationResult(principal, interest, amount, rate, timeInYears)

    @Test
    void anInfiniteResultIsRefused() {
        CalculationResult result = new CalculationResult(1, 1, Double.POSITIVE_INFINITY, 1, 1);

        assertThrows(InvalidRequestException.class, () -> RequestChecks.checkResult(result));
    }

    @Test
    void aNormalResultPasses() {
        CalculationResult result = new CalculationResult(8000, 1120, 9120, 7, 2);

        assertDoesNotThrow(() -> RequestChecks.checkResult(result));
    }
}
