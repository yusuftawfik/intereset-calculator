package com.interestcalculator.backend;

// All the simple interest formulas from your slides.
//
//   I = P * i * t        (interest)
//   S = P + I            (amount)
//
// p      = principal (money you start with)
// rate   = rate in PERCENT (7 means 7%). The methods turn it into a decimal for you.
// years  = time in YEARS (use TimeConverter first if you have months, weeks or days)

public class SimpleInterest {

    // ---------- Find the interest (I) ----------
    public static double interest(double p, double rate, double years) {
        double i = rate / 100;
        return p * i * years;
    }

    // ---------- Find the amount (S) ----------
    public static double amount(double p, double rate, double years) {
        return p + interest(p, rate, years);
    }

    // ---------- Find the principal (P) ----------
    // when you know the interest:  P = I / (i * t)
    public static double principalFromInterest(double interest, double rate, double years) {
        double i = rate / 100;
        return interest / (i * years);
    }

    // when you know the amount:  S = P * (1 + i * t)  so  P = S / (1 + i * t)
    public static double principalFromAmount(double amount, double rate, double years) {
        double i = rate / 100;
        return amount / (1 + i * years);
    }

    // ---------- Find the rate (answer is in PERCENT) ----------
    // when you know the interest:  i = I / (P * t)
    public static double rateFromInterest(double interest, double p, double years) {
        double i = interest / (p * years);
        return i * 100;
    }

    // when you know the amount:  I = S - P  then the same formula
    public static double rateFromAmount(double amount, double p, double years) {
        return rateFromInterest(amount - p, p, years);
    }

    // ---------- Find the time (answer is in YEARS) ----------
    // when you know the interest:  t = I / (P * i)
    public static double timeFromInterest(double interest, double p, double rate) {
        double i = rate / 100;
        return interest / (p * i);
    }

    // when you know the amount:  I = S - P  then the same formula
    public static double timeFromAmount(double amount, double p, double rate) {
        return timeFromInterest(amount - p, p, rate);
    }
}
