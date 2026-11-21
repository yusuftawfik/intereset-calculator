package com.interestcalculator.service;

import com.interestcalculator.backend.CompoundInterest;
import com.interestcalculator.backend.TimeConverter;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.CompoundRequest;
import com.interestcalculator.model.Find;
import org.springframework.stereotype.Service;

@Service
public class CompoundInterestService {

    public CalculationResult calculate(CompoundRequest request) {
        RequestChecks.checkCompound(request);

        Find find = request.find();
        int m = request.timesPerYear();

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
            p = CompoundInterest.principal(request.amount(), rate, m, years);

        } else if (find == Find.RATE) {
            p = request.principal();
            years = TimeConverter.toYears(request.time(), request.unit());
            rate = CompoundInterest.rate(request.amount(), p, m, years);

        } else {
            // Find.TIME
            p = request.principal();
            rate = request.rate();
            years = CompoundInterest.time(request.amount(), p, rate, m);
        }

        // Now we know P, rate and time, so we can work out the rest
        double amount = CompoundInterest.amount(p, rate, m, years);
        double interest = CompoundInterest.interest(p, rate, m, years);

        CalculationResult result = new CalculationResult(p, interest, amount, rate, years);
        RequestChecks.checkResult(result);
        return result;
    }
}
