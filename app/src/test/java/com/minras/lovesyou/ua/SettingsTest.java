package com.minras.lovesyou.ua;

import android.content.SharedPreferences;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SettingsTest {

    @Test
    public void migratesRecognizedSavedAccountsToEnabledPreferenceAndRemovesEmail() {
        for (String email : new String[] {
                "ol.zvereva@gmail.com",
                "andrey.shchurkov@gmail.com"
        }) {
            Map<String, Object> values = new HashMap<>();
            values.put(Settings.LEGACY_ACCOUNT_EMAIL_KEY, "  " + email + "  ");
            SharedPreferences preferences = preferences(values);

            Settings settings = new Settings();
            settings.init(preferences);

            assertTrue(settings.arePersonalQuotesEnabled());
            assertEquals(Boolean.TRUE, values.get(Settings.PERSONAL_QUOTES_ENABLED_KEY));
            assertFalse(values.containsKey(Settings.LEGACY_ACCOUNT_EMAIL_KEY));
        }
    }

    @Test
    public void migratesUnrecognizedOrMissingSavedAccountToDisabledPreference() {
        Map<String, Object> values = new HashMap<>();
        values.put(Settings.LEGACY_ACCOUNT_EMAIL_KEY, "someone@example.com");
        SharedPreferences preferences = preferences(values);

        Settings settings = new Settings();
        settings.init(preferences);

        assertFalse(settings.arePersonalQuotesEnabled());
        assertEquals(Boolean.FALSE, values.get(Settings.PERSONAL_QUOTES_ENABLED_KEY));
        assertFalse(values.containsKey(Settings.LEGACY_ACCOUNT_EMAIL_KEY));
    }

    @Test
    public void savedPreferenceOverridesLegacyEmailAndRemovesIt() {
        Map<String, Object> values = new HashMap<>();
        values.put(Settings.PERSONAL_QUOTES_ENABLED_KEY, false);
        values.put(Settings.LEGACY_ACCOUNT_EMAIL_KEY, "andrey.shchurkov@gmail.com");
        SharedPreferences preferences = preferences(values);

        Settings settings = new Settings();
        settings.init(preferences);

        assertFalse(settings.arePersonalQuotesEnabled());
        assertEquals(Boolean.FALSE, values.get(Settings.PERSONAL_QUOTES_ENABLED_KEY));
        assertFalse(values.containsKey(Settings.LEGACY_ACCOUNT_EMAIL_KEY));
    }

    @Test
    public void savesPreferenceChangesWithoutRetainingLegacyEmail() {
        Map<String, Object> values = new HashMap<>();
        values.put(Settings.LEGACY_ACCOUNT_EMAIL_KEY, "ol.zvereva@gmail.com");
        SharedPreferences preferences = preferences(values);
        Settings settings = new Settings();
        settings.setPersonalQuotesEnabled(false);

        settings.save(preferences);

        assertEquals(Boolean.FALSE, values.get(Settings.PERSONAL_QUOTES_ENABLED_KEY));
        assertFalse(values.containsKey(Settings.LEGACY_ACCOUNT_EMAIL_KEY));

        Settings reloadedSettings = new Settings();
        reloadedSettings.init(preferences);
        assertFalse(reloadedSettings.arePersonalQuotesEnabled());
    }

    @Test
    public void savesAndRestoresBirthdayAsMonthAndDay() {
        Map<String, Object> values = new HashMap<>();
        SharedPreferences preferences = preferences(values);
        Settings settings = new Settings();
        settings.setBirthday(2, 29);

        settings.save(preferences);

        assertEquals(2, values.get(Settings.BIRTHDAY_MONTH_KEY));
        assertEquals(29, values.get(Settings.BIRTHDAY_DAY_KEY));

        Settings restoredSettings = new Settings();
        restoredSettings.init(preferences);

        assertEquals(2, restoredSettings.getBirthday().getMonth());
        assertEquals(29, restoredSettings.getBirthday().getDay());
    }

    @Test
    public void clearingBirthdayRemovesBothPersistedParts() {
        Map<String, Object> values = new HashMap<>();
        values.put(Settings.BIRTHDAY_MONTH_KEY, 7);
        values.put(Settings.BIRTHDAY_DAY_KEY, 16);
        SharedPreferences preferences = preferences(values);
        Settings settings = new Settings();
        settings.init(preferences);

        settings.clearBirthday();
        settings.save(preferences);

        assertFalse(values.containsKey(Settings.BIRTHDAY_MONTH_KEY));
        assertFalse(values.containsKey(Settings.BIRTHDAY_DAY_KEY));

        Settings restoredSettings = new Settings();
        restoredSettings.init(preferences);
        assertNull(restoredSettings.getBirthday());
    }

    @Test
    public void discardsInvalidOrPartialPersistedBirthday() {
        assertInvalidBirthdayIsDiscarded(birthdayValues(2, 30));
        assertInvalidBirthdayIsDiscarded(birthdayValues(2, null));
    }

    private void assertInvalidBirthdayIsDiscarded(Map<String, Object> values) {
        SharedPreferences preferences = preferences(values);
        Settings settings = new Settings();
        settings.init(preferences);

        assertNull(settings.getBirthday());
        assertFalse(values.containsKey(Settings.BIRTHDAY_MONTH_KEY));
        assertFalse(values.containsKey(Settings.BIRTHDAY_DAY_KEY));
    }

    private Map<String, Object> birthdayValues(Integer month, Integer day) {
        Map<String, Object> values = new HashMap<>();
        if (month != null) {
            values.put(Settings.BIRTHDAY_MONTH_KEY, month);
        }
        if (day != null) {
            values.put(Settings.BIRTHDAY_DAY_KEY, day);
        }
        return values;
    }

    private SharedPreferences preferences(Map<String, Object> values) {
        return (SharedPreferences) Proxy.newProxyInstance(
                SharedPreferences.class.getClassLoader(),
                new Class<?>[] {SharedPreferences.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "contains":
                                return values.containsKey(args[0]);
                            case "getString":
                                return values.containsKey(args[0]) ? values.get(args[0]) : args[1];
                            case "getBoolean":
                                return values.containsKey(args[0]) ? values.get(args[0]) : args[1];
                            case "getInt":
                                return values.containsKey(args[0]) ? values.get(args[0]) : args[1];
                            case "edit":
                                return editor(values);
                            default:
                                throw new UnsupportedOperationException(method.getName());
                        }
                    }
                });
    }

    private SharedPreferences.Editor editor(Map<String, Object> values) {
        return (SharedPreferences.Editor) Proxy.newProxyInstance(
                SharedPreferences.Editor.class.getClassLoader(),
                new Class<?>[] {SharedPreferences.Editor.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "putBoolean":
                            case "putInt":
                                values.put((String) args[0], args[1]);
                                return proxy;
                            case "remove":
                                values.remove(args[0]);
                                return proxy;
                            case "apply":
                                return null;
                            default:
                                throw new UnsupportedOperationException(method.getName());
                        }
                    }
                });
    }
}
