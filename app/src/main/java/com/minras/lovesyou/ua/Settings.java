package com.minras.lovesyou.ua;

import android.content.SharedPreferences;

public class Settings {
    private String accountEmail = null;

    static final private String ACCOUNT_EMAIL_KEY = "accountEmail";

    public String getAccountEmail() {
        return accountEmail;
    }

    public boolean applyAccountPickerResult(boolean successful, String selectedAccountEmail) {
        if (!successful) {
            return false;
        }

        String normalizedEmail = normalizeAccountEmail(selectedAccountEmail);
        if (normalizedEmail == null) {
            return false;
        }

        accountEmail = normalizedEmail;
        return true;
    }

    public void clearAccountEmail() {
        accountEmail = null;
    }

    public void init(SharedPreferences sharedPreferences) {
        accountEmail = normalizeAccountEmail(sharedPreferences.getString(ACCOUNT_EMAIL_KEY, null));
    }

    public void save(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (accountEmail == null) {
            editor.remove(ACCOUNT_EMAIL_KEY);
        } else {
            editor.putString(ACCOUNT_EMAIL_KEY, accountEmail);
        }
        editor.apply();
    }

    private String normalizeAccountEmail(String email) {
        if (email == null) {
            return null;
        }

        String normalizedEmail = email.trim();
        return normalizedEmail.isEmpty() ? null : normalizedEmail;
    }
}
