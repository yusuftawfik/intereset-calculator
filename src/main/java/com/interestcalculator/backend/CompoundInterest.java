package com.interestcalculator.backend;

// All the compound interest formulas from slides
//
//   S = P * (1 + i)^n
//   i = rate / m          (rate per period)
//   n = m * years         (number of periods)
//
// p      = principal
// rate   = YEARLY rate in PERCENT (8 means 8%)
// m      = times per year the interest is added
//          1 = yearly, 2 = half-yearly, 4 = quarterly, 12 = monthly, 52 = weekly, 360 = daily
// years  = time in YEARS


public class CompoundInterest {

    // ---------- Find the amount (S) ----------
    public static double amount(double p, double rate, int m, double years) {
        double i = rate / 100 / m;
        double n = m * years;
        return p * Math.pow(1 + i, n);
    }

    // ---------- Find the interest (I = S - P) ----------
    public static double interest(double p, double rate, int m, double years) {
        return amount(p, rate, m, years) - p;
    }

    // ---------- Find the principal (P) ----------
    // P = S / (1 + i)^n
    public static double principal(double amount, double rate, int m, double years) {
        double i = rate / 100 / m;
        double n = m * years;
        return amount / Math.pow(1 + i, n);
    }

    // ---------- Find the yearly rate (answer is in PERCENT) ----------
    // (1 + i)^n = S / P   so   i = (S / P)^(1/n) - 1   and   yearly rate = i * m
    public static double rate(double amount, double p, int m, double years) {
        double n = m * years;
        double i = Math.pow(amount / p, 1 / n) - 1;
        return i * m * 100;
    }

    // ---------- Find the time (answer is in YEARS) ----------
    // n = ln(S / P) / ln(1 + i)   and   years = n / m
    public static double time(double amount, double p, double rate, int m) {
        double i = rate / 100 / m;
        double n = Math.log(amount / p) / Math.log(1 + i);
        return n / m;
    }
}
