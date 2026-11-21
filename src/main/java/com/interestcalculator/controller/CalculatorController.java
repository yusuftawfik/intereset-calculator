package com.interestcalculator.controller;

import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.CompoundRequest;
import com.interestcalculator.model.SimpleRequest;
import com.interestcalculator.service.CompoundInterestService;
import com.interestcalculator.service.SimpleInterestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// The "controller" is the front desk: it receives the request, passes it to the
// right service, and sends the answer back. It does no maths itself.
@RestController
@RequestMapping("/api")   // every address in this class starts with /api
public class CalculatorController {

    private final SimpleInterestService simpleService;
    private final CompoundInterestService compoundService;

    // Spring sees that this class needs the two services, and hands them over
    // automatically when it creates the controller.
    public CalculatorController(SimpleInterestService simpleService,
                                CompoundInterestService compoundService) {
        this.simpleService = simpleService;
        this.compoundService = compoundService;
    }

    // @PostMapping = this address accepts data that is SENT to it.
    // @RequestBody = "turn the data that arrived into a SimpleRequest box".
    // @Valid       = "check the rules written on that box before going any further".
    @PostMapping("/simple")
    public CalculationResult simple(@Valid @RequestBody SimpleRequest request) {
        return simpleService.calculate(request);
    }

    @PostMapping("/compound")
    public CalculationResult compound(@Valid @RequestBody CompoundRequest request) {
        return compoundService.calculate(request);
    }
}
