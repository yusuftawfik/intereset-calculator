package com.interestcalculator.service;

import com.interestcalculator.backend.SimpleInterest;
import com.interestcalculator.backend.TimeConverter;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.Find;
import com.interestcalculator.model.Known;
import com.interestcalculator.model.SimpleRequest;
import org.springframework.stereotype.Service;

// The "service" is the middle layer. It decides WHICH of your calculation methods to use.
// @Service is a label that tells Spring: "create one of these and keep it ready".
@Service
public class SimpleInterestService {

    public CalculationResult calculate(SimpleRequest request) {
        RequestChecks.checkSimple(request);

        Find find = request.find();
        Known known = request.known();

        double p;
        double rate;
        double years;

        if (find == Find.AMOUNT) {
            p = request.principal();
            rate = request.rate();
            years = TimeConverter.toYears(request.time(), request.unit());

        } else if (find == Find.PRINCIPAL) {
            rate = request.rate();
            years = TimeConverter.toYears(request.time(), request.unit());
            if (known == Known.INTEREST) {
                p = SimpleInterest.principalFromInterest(request.interest(), rate, years);
            } else {
                p = SimpleInterest.principalFromAmount(request.amount(), rate, years);
            }

        } else if (find == Find.RATE) {
            p = request.principal();
            years = TimeConverter.toYears(request.time(), request.unit());
            if (known == Known.INTEREST) {
                rate = SimpleInterest.rateFromInterest(request.interest(), p, years);
            } else {
                rate = SimpleInterest.rateFromAmount(request.amount(), p, years);
            }

        } else {
            // Find.TIME
            p = request.principal();
            rate = request.rate();
            if (known == Known.INTEREST) {
                years = SimpleInterest.timeFromInterest(request.interest(), p, rate);
            } else {
                years = SimpleInterest.timeFromAmount(request.amount(), p, rate);
            }
        }

        // Now we know P, rate and time, so we can work out the rest
        double interest = SimpleInterest.interest(p, rate, years);
        double amount = SimpleInterest.amount(p, rate, years);

        CalculationResult result = new CalculationResult(p, interest, amount, rate, years);
        RequestChecks.checkResult(result);
        return result;
    }
}
