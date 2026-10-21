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
