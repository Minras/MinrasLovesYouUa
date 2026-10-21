package com.minras.lovesyou.ua;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SettingsTest {

    @Test
    public void successfulPickerResultStoresNonBlankAccountName() {
        Settings settings = new Settings();

        assertTrue(settings.applyAccountPickerResult(true, "  user@example.com  "));
        assertEquals("user@example.com", settings.getAccountEmail());
    }

    @Test
    public void cancellationPreservesPreviouslySavedAccount() {
        Settings settings = linkedSettings();

        assertFalse(settings.applyAccountPickerResult(false, null));
        assertEquals("user@example.com", settings.getAccountEmail());
    }

    @Test
    public void missingOrBlankPickerAccountPreservesPreviouslySavedAccount() {
        Settings settings = linkedSettings();

        assertFalse(settings.applyAccountPickerResult(true, null));
        assertEquals("user@example.com", settings.getAccountEmail());

        assertFalse(settings.applyAccountPickerResult(true, "   "));
        assertEquals("user@example.com", settings.getAccountEmail());
    }

    @Test
    public void unlinkingClearsTheAccountExplicitly() {
        Settings settings = linkedSettings();

        settings.clearAccountEmail();

        assertNull(settings.getAccountEmail());
    }

    private Settings linkedSettings() {
        Settings settings = new Settings();
        assertTrue(settings.applyAccountPickerResult(true, "user@example.com"));
        return settings;
    }
}
