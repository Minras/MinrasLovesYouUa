package com.minras.lovesyou.ua;

import android.content.SharedPreferences;

public class Settings {
    static final String PERSONAL_QUOTES_ENABLED_KEY = "personalQuotesEnabled";
    static final String LEGACY_ACCOUNT_EMAIL_KEY = "accountEmail";
    static final String BIRTHDAY_MONTH_KEY = "birthdayMonth";
    static final String BIRTHDAY_DAY_KEY = "birthdayDay";

    private boolean personalQuotesEnabled;
    private Birthday birthday;

    public boolean arePersonalQuotesEnabled() {
        return personalQuotesEnabled;
    }

    public void setPersonalQuotesEnabled(boolean enabled) {
        personalQuotesEnabled = enabled;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(int month, int day) {
        birthday = new Birthday(month, day);
    }

    public void clearBirthday() {
        birthday = null;
    }

    public void init(SharedPreferences sharedPreferences) {
        boolean hasSavedPreference = sharedPreferences.contains(PERSONAL_QUOTES_ENABLED_KEY);
        String legacyAccountEmail = sharedPreferences.getString(LEGACY_ACCOUNT_EMAIL_KEY, null);
        boolean hasBirthdayMonth = sharedPreferences.contains(BIRTHDAY_MONTH_KEY);
        boolean hasBirthdayDay = sharedPreferences.contains(BIRTHDAY_DAY_KEY);
        personalQuotesEnabled = hasSavedPreference
                ? sharedPreferences.getBoolean(PERSONAL_QUOTES_ENABLED_KEY, false)
                : isRecognizedPersonalAccount(legacyAccountEmail);

        boolean invalidBirthday = hasBirthdayMonth != hasBirthdayDay;
        birthday = null;
        if (hasBirthdayMonth && hasBirthdayDay) {
            try {
                birthday = new Birthday(
                        sharedPreferences.getInt(BIRTHDAY_MONTH_KEY, 0),
                        sharedPreferences.getInt(BIRTHDAY_DAY_KEY, 0));
            } catch (IllegalArgumentException exception) {
                invalidBirthday = true;
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!hasSavedPreference) {
            editor.putBoolean(PERSONAL_QUOTES_ENABLED_KEY, personalQuotesEnabled);
        }
        editor.remove(LEGACY_ACCOUNT_EMAIL_KEY);
        if (invalidBirthday) {
            editor.remove(BIRTHDAY_MONTH_KEY);
            editor.remove(BIRTHDAY_DAY_KEY);
        }
        editor.apply();
    }

    public void save(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PERSONAL_QUOTES_ENABLED_KEY, personalQuotesEnabled);
        editor.remove(LEGACY_ACCOUNT_EMAIL_KEY);
        if (birthday == null) {
            editor.remove(BIRTHDAY_MONTH_KEY);
            editor.remove(BIRTHDAY_DAY_KEY);
        } else {
            editor.putInt(BIRTHDAY_MONTH_KEY, birthday.getMonth());
            editor.putInt(BIRTHDAY_DAY_KEY, birthday.getDay());
        }
        editor.apply();
    }

    private static boolean isRecognizedPersonalAccount(String email) {
        if (email == null) {
            return false;
        }

        String normalizedEmail = email.trim();
        return "ol.zvereva@gmail.com".equals(normalizedEmail)
                || "andrey.shchurkov@gmail.com".equals(normalizedEmail);
    }
}
