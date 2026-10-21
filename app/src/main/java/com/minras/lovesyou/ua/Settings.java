package com.minras.lovesyou.ua;

import android.content.SharedPreferences;

public class Settings {
    static final String PERSONAL_QUOTES_ENABLED_KEY = "personalQuotesEnabled";
    static final String LEGACY_ACCOUNT_EMAIL_KEY = "accountEmail";

    private boolean personalQuotesEnabled;

    public boolean arePersonalQuotesEnabled() {
        return personalQuotesEnabled;
    }

    public void setPersonalQuotesEnabled(boolean enabled) {
        personalQuotesEnabled = enabled;
    }

    public void init(SharedPreferences sharedPreferences) {
        boolean hasSavedPreference = sharedPreferences.contains(PERSONAL_QUOTES_ENABLED_KEY);
        String legacyAccountEmail = sharedPreferences.getString(LEGACY_ACCOUNT_EMAIL_KEY, null);
        personalQuotesEnabled = hasSavedPreference
                ? sharedPreferences.getBoolean(PERSONAL_QUOTES_ENABLED_KEY, false)
                : isRecognizedPersonalAccount(legacyAccountEmail);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!hasSavedPreference) {
            editor.putBoolean(PERSONAL_QUOTES_ENABLED_KEY, personalQuotesEnabled);
        }
        editor.remove(LEGACY_ACCOUNT_EMAIL_KEY);
        editor.apply();
    }

    public void save(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PERSONAL_QUOTES_ENABLED_KEY, personalQuotesEnabled);
        editor.remove(LEGACY_ACCOUNT_EMAIL_KEY);
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
