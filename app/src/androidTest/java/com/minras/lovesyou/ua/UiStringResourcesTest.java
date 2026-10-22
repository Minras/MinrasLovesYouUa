package com.minras.lovesyou.ua;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class UiStringResourcesTest {

    private static final int[] VISIBLE_UI_STRINGS = {
            R.string.app_name,
            R.string.action_copyright,
            R.string.title_activity_copyright,
            R.string.action_settings,
            R.string.title_activity_settings,
            R.string.personal_quotes_enabled,
            R.string.personal_quotes_description,
            R.string.birthday_setting_title,
            R.string.birthday_not_set,
            R.string.birthday_summary_set,
            R.string.birthday_setting_description,
            R.string.birthday_set_button,
            R.string.birthday_change_button,
            R.string.birthday_clear_button,
            R.string.birthday_picker_title,
            R.string.tell_btn_title,
            R.string.ask_btn_title,
            R.string.love_text_frame_descr,
            R.string.tell_btn_description,
            R.string.ask_btn_description,
            R.string.copyright_credits
    };

    @Test
    public void visibleStringsResolveForDefaultAndUkrainianLocales() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context defaultContext = contextForLocale(appContext, "en");
        Context ukrainianContext = contextForLocale(appContext, "uk");

        for (int stringId : VISIBLE_UI_STRINGS) {
            String defaultValue = defaultContext.getString(stringId);
            String ukrainianValue = ukrainianContext.getString(stringId);

            assertFalse("String resource " + stringId + " should not be empty", defaultValue.isEmpty());
            assertFalse("String resource " + stringId + " should not be empty", ukrainianValue.isEmpty());
            assertEquals("Ukrainian locale should use Ukrainian UI copy", defaultValue, ukrainianValue);
        }
    }

    private static Context contextForLocale(Context context, String languageTag) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocales(LocaleList.forLanguageTags(languageTag));
        return context.createConfigurationContext(configuration);
    }
}
