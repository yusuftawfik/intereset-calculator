package com.interestcalculator.backend;

// Converts time between years, months, weeks and days.
// "static" means you call these methods with the class name:
//     double t = TimeConverter.toYears(90, 4);
public class TimeConverter {

    // Time units (use these numbers when you call the methods)
    // 1 = years
    // 2 = months
    // 3 = weeks
    // 4 = days, ordinary interest (360-day year)  <- the default in your slides
    // 5 = days, exact interest (365-day year)
    // 6 = days, exact interest in a leap year (366-day year)

    // Turns an amount of time into years
    public static double toYears(double time, int unit) {
        if (unit == 1) {
            return time;
        } else if (unit == 2) {
            return time / 12;
        } else if (unit == 3) {
            return time / 52;
        } else if (unit == 4) {
            return time / 360;
        } else if (unit == 5) {
            return time / 365;
        } else if (unit == 6) {
            return time / 366;
        } else {
            return -1; // -1 means "this unit does not exist"
        }
    }

    // Turns years back into another unit (the reverse of toYears)
    public static double fromYears(double years, int unit) {
        if (unit == 1) {
            return years;
        } else if (unit == 2) {
            return years * 12;
        } else if (unit == 3) {
            return years * 52;
        } else if (unit == 4) {
            return years * 360;
        } else if (unit == 5) {
            return years * 365;
        } else if (unit == 6) {
            return years * 366;
        } else {
            return -1;
        }
    }
}
