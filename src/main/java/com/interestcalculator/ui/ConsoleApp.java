package com.interestcalculator.ui;

import com.interestcalculator.backend.CompoundInterest;
import com.interestcalculator.backend.SimpleInterest;
import com.interestcalculator.backend.TimeConverter;

import java.util.Scanner;

// The console (text) version of the calculator.
// It only asks questions and prints answers. ALL the maths is in the backend folder.
// Later, the front end will replace this file and call the same backend methods.
public class ConsoleApp {

    public static void start() {
        Scanner input = new Scanner(System.in);

        System.out.printf("Interest calculator%n");
        System.out.printf("1) Simple interest%n");
        System.out.printf("2) Compound interest%n");
        int type = input.nextInt();

        if (type == 1) {
            simpleMenu(input);
        } else if (type == 2) {
            compoundMenu(input);
        } else {
            System.out.printf("Invalid choice%n");
        }

        input.close();
    }

    // ------------------------------------------------------------
    // SIMPLE INTEREST
    // ------------------------------------------------------------
    private static void simpleMenu(Scanner input) {
        System.out.printf("What do you want to find?%n");
        System.out.printf("1) Interest (I) and Amount (S)%n");
        System.out.printf("2) Principal (P)%n");
        System.out.printf("3) Rate (r)%n");
        System.out.printf("4) Time (t)%n");
        int find = input.nextInt();

        if (find < 1 || find > 4) {
            System.out.printf("Invalid choice%n");
            return;
        }

        // Time is only asked when we are NOT finding time
        double years = 0;
        if (find != 4) {
            years = askYears(input);
            if (years < 0) {
                System.out.printf("Invalid time unit%n");
                return;
            }
        }

        if (find == 1) {
            double p = askNumber(input, "Enter the principal (P): ");
            double rate = askNumber(input, "Enter the rate in %: ");

            double interest = SimpleInterest.interest(p, rate, years);
            double amount = SimpleInterest.amount(p, rate, years);

            System.out.printf("Interest = $%.2f%n", interest);
            System.out.printf("Amount = $%.2f%n", amount);

        } else {
            System.out.printf("What do you know?  1) Interest (I)  2) Amount (S)%n");
            int known = input.nextInt();

            if (known != 1 && known != 2) {
                System.out.printf("Invalid choice%n");
                return;
            }

            if (find == 2) {
                // Find the principal
                double rate = askNumber(input, "Enter the rate in %: ");
                double p;

                if (known == 1) {
                    double interest = askNumber(input, "Enter the interest (I): ");
                    p = SimpleInterest.principalFromInterest(interest, rate, years);
                } else {
                    double amount = askNumber(input, "Enter the amount (S): ");
                    p = SimpleInterest.principalFromAmount(amount, rate, years);
                }

                System.out.printf("Principal = $%.2f%n", p);

            } else if (find == 3) {
                // Find the rate
                double p = askNumber(input, "Enter the principal (P): ");
                double rate;

                if (known == 1) {
                    double interest = askNumber(input, "Enter the interest (I): ");
                    rate = SimpleInterest.rateFromInterest(interest, p, years);
                } else {
                    double amount = askNumber(input, "Enter the amount (S): ");
                    rate = SimpleInterest.rateFromAmount(amount, p, years);
                }

                System.out.printf("Rate = %.2f%%%n", rate);

            } else {
                // Find the time
                double p = askNumber(input, "Enter the principal (P): ");
                double rate = askNumber(input, "Enter the rate in %: ");
                double t;

                if (known == 1) {
                    double interest = askNumber(input, "Enter the interest (I): ");
                    t = SimpleInterest.timeFromInterest(interest, p, rate);
                } else {
                    double amount = askNumber(input, "Enter the amount (S): ");
                    t = SimpleInterest.timeFromAmount(amount, p, rate);
                }

                double days = TimeConverter.fromYears(t, 4);
                System.out.printf("Time = %.4f years (about %.0f days)%n", t, days);
            }
        }
    }

    // ------------------------------------------------------------
    // COMPOUND INTEREST
    // ------------------------------------------------------------
    private static void compoundMenu(Scanner input) {
        System.out.printf("What do you want to find?%n");
        System.out.printf("1) Amount (S) and Interest (I)%n");
        System.out.printf("2) Principal (P)%n");
        System.out.printf("3) Rate (r)%n");
        System.out.printf("4) Time (t)%n");
        int find = input.nextInt();

        if (find < 1 || find > 4) {
            System.out.printf("Invalid choice%n");
            return;
        }

        System.out.printf("How many times a year is interest added?%n");
        System.out.printf("1 = yearly, 2 = half-yearly, 4 = quarterly, 12 = monthly, 52 = weekly, 360 = daily%n");
        int m = input.nextInt();

        if (m < 1) {
            System.out.printf("Invalid number of times per year%n");
            return;
        }

        double years = 0;
        if (find != 4) {
            years = askYears(input);
            if (years < 0) {
                System.out.printf("Invalid time unit%n");
                return;
            }
        }

        if (find == 1) {
            double p = askNumber(input, "Enter the principal (P): ");
            double rate = askNumber(input, "Enter the yearly rate in %: ");

            double amount = CompoundInterest.amount(p, rate, m, years);
            double interest = CompoundInterest.interest(p, rate, m, years);

            System.out.printf("Amount = $%.2f%n", amount);
            System.out.printf("Interest = $%.2f%n", interest);

        } else if (find == 2) {
            double amount = askNumber(input, "Enter the amount (S): ");
            double rate = askNumber(input, "Enter the yearly rate in %: ");

            double p = CompoundInterest.principal(amount, rate, m, years);
            System.out.printf("Principal = $%.2f%n", p);

        } else if (find == 3) {
            double amount = askNumber(input, "Enter the amount (S): ");
            double p = askNumber(input, "Enter the principal (P): ");

            double rate = CompoundInterest.rate(amount, p, m, years);
            System.out.printf("Yearly rate = %.2f%%%n", rate);

        } else {
            double amount = askNumber(input, "Enter the amount (S): ");
            double p = askNumber(input, "Enter the principal (P): ");
            double rate = askNumber(input, "Enter the yearly rate in %: ");

            double t = CompoundInterest.time(amount, p, rate, m);
            double days = TimeConverter.fromYears(t, 4);
            System.out.printf("Time = %.4f years (about %.0f days)%n", t, days);
        }
    }

    // ------------------------------------------------------------
    // Small helpers used by both menus
    // ------------------------------------------------------------

    // Prints a question and returns the number the user types
    private static double askNumber(Scanner input, String message) {
        System.out.printf("%s", message);
        return input.nextDouble();
    }

    // Asks for a time unit and an amount of time, and returns it in years
    private static double askYears(Scanner input) {
        System.out.printf("Time unit:%n");
        System.out.printf("1) Years  2) Months  3) Weeks%n");
        System.out.printf("4) Days (360-day year)  5) Days (365-day year)  6) Days (366-day year)%n");
        int unit = input.nextInt();

        double time = askNumber(input, "Enter the amount of time: ");
        double years = TimeConverter.toYears(time, unit);

        if (years >= 0) {
            System.out.printf("Time in years = %.4f%n", years);
        }
        return years;
    }
}
