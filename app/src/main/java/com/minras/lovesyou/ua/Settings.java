package com.minras.lovesyou.ua;

import android.content.SharedPreferences;

import java.io.Serializable;

public class Settings implements Serializable {
    private String accountEmail = null;

    static final private String ACCOUNT_EMAIL_KEY = "accountEmail";

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public void init(SharedPreferences sharedPreferences) {
        setAccountEmail(sharedPreferences.getString(ACCOUNT_EMAIL_KEY, null));
    }

    public void save(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCOUNT_EMAIL_KEY, accountEmail);
        editor.apply();
    }
}
