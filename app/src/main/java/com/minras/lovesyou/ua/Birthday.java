package com.minras.lovesyou.ua;

/** A recurring birthday represented by its month and day, without a year. */
final class Birthday {

    private static final int[] DAYS_IN_MONTH = {
            31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    private final int month;
    private final int day;

    Birthday(int month, int day) {
        if (month < 1 || month > 12 || day < 1 || day > DAYS_IN_MONTH[month - 1]) {
            throw new IllegalArgumentException("Invalid birthday month and day");
        }
        this.month = month;
        this.day = day;
    }

    int getMonth() {
        return month;
    }

    int getDay() {
        return day;
    }

    boolean matches(int candidateMonth, int candidateDay) {
        return month == candidateMonth && day == candidateDay;
    }
}
