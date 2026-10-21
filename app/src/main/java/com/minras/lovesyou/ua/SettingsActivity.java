package com.minras.lovesyou.ua;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        settings = new Settings();
        settings.init(sharedPreferences);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SwitchCompat personalQuotesSwitch = findViewById(R.id.personal_quotes_switch);
        personalQuotesSwitch.setChecked(settings.arePersonalQuotesEnabled());
        personalQuotesSwitch.setOnCheckedChangeListener((button, enabled) -> {
            settings.setPersonalQuotesEnabled(enabled);
            settings.save(sharedPreferences);
        });
    }
}
