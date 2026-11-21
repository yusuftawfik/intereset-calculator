package com.interestcalculator.service;

import com.interestcalculator.exception.InvalidRequestException;
import com.interestcalculator.model.CalculationResult;
import com.interestcalculator.model.CompoundRequest;
import com.interestcalculator.model.Find;
import com.interestcalculator.model.Known;
import com.interestcalculator.model.SimpleRequest;

import java.util.ArrayList;
import java.util.List;

// The checks that depend on WHAT the user wants to find.
// For example: to find the rate you must send the principal, the time and the interest.
// The labels on the request boxes can't know that, so we check it here.
// All the problems are collected, so the user sees everything wrong at once.
public class RequestChecks {

    // ---------- Simple interest ----------
    public static void checkSimple(SimpleRequest r) {
        List<String> errors = new ArrayList<>();
        Find find = r.find();

        if (find != Find.AMOUNT && r.known() == null) {
            errors.add("Please say what you know: INTEREST or AMOUNT (the known field)");
        }

        if (find == Find.AMOUNT) {
            need(errors, r.principal(), "principal");
            need(errors, r.rate(), "rate");
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else if (find == Find.PRINCIPAL) {
            needKnownValue(errors, r);
            need(errors, r.rate(), "rate");
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else if (find == Find.RATE) {
            need(errors, r.principal(), "principal");
            needKnownValue(errors, r);
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else {
            // Find.TIME
            need(errors, r.principal(), "principal");
            need(errors, r.rate(), "rate");
            needKnownValue(errors, r);
        }

        // When finding the rate or the time from the amount, the amount must be bigger
        // than the principal, otherwise the interest would be zero or negative.
        boolean findsRateOrTime = find == Find.RATE || find == Find.TIME;
        if (findsRateOrTime && r.known() == Known.AMOUNT
                && r.amount() != null && r.principal() != null
                && r.amount() <= r.principal()) {
            errors.add("The amount must be greater than the principal");
        }

        throwIfAny(errors);
    }

    // ---------- Compound interest ----------
    public static void checkCompound(CompoundRequest r) {
        List<String> errors = new ArrayList<>();
        Find find = r.find();

        Integer m = r.timesPerYear();
        if (m == null) {
            errors.add("Please enter how many times a year interest is added (timesPerYear)");
        } else if (m != 1 && m != 2 && m != 4 && m != 12 && m != 52 && m != 360) {
            errors.add("timesPerYear must be 1, 2, 4, 12, 52 or 360");
        }

        if (find == Find.AMOUNT) {
            need(errors, r.principal(), "principal");
            need(errors, r.rate(), "rate");
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else if (find == Find.PRINCIPAL) {
            need(errors, r.amount(), "amount");
            need(errors, r.rate(), "rate");
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else if (find == Find.RATE) {
            need(errors, r.amount(), "amount");
            need(errors, r.principal(), "principal");
            need(errors, r.time(), "time");
            need(errors, r.unit(), "time unit");

        } else {
            // Find.TIME
            need(errors, r.amount(), "amount");
            need(errors, r.principal(), "principal");
            need(errors, r.rate(), "rate");
        }

        boolean findsRateOrTime = find == Find.RATE || find == Find.TIME;
        if (findsRateOrTime && r.amount() != null && r.principal() != null
                && r.amount() <= r.principal()) {
            errors.add("The amount must be greater than the principal");
        }

        throwIfAny(errors);
    }

    // ---------- Check the answer before sending it ----------
    // Very big numbers can turn into "infinity", which can't be shown. Catch that here.
    public static void checkResult(CalculationResult result) {
        boolean usable = Double.isFinite(result.principal())
                && Double.isFinite(result.interest())
                && Double.isFinite(result.amount())
                && Double.isFinite(result.rate())
                && Double.isFinite(result.timeInYears());

        if (!usable) {
            throw new InvalidRequestException(
                    "These numbers give a result that is too big or cannot be calculated");
        }
    }

    // ---------- Small helpers ----------
    private static void need(List<String> errors, Object value, String name) {
        if (value == null) {
            errors.add("Please enter the " + name);
        }
    }

    // If the user said they know the interest, we need the interest; if the amount, the amount.
    private static void needKnownValue(List<String> errors, SimpleRequest r) {
        if (r.known() == Known.INTEREST) {
            need(errors, r.interest(), "interest");
        } else if (r.known() == Known.AMOUNT) {
            need(errors, r.amount(), "amount");
        }
    }

    private static void throwIfAny(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(errors);
        }
    }
}
