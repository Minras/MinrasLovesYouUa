package com.minras.lovesyou.ua;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

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

        Button birthdayPickerButton = findViewById(R.id.birthday_picker_button);
        Button birthdayClearButton = findViewById(R.id.birthday_clear_button);
        birthdayPickerButton.setOnClickListener(view -> showBirthdayPicker());
        birthdayClearButton.setOnClickListener(view -> {
            settings.clearBirthday();
            settings.save(sharedPreferences);
            updateBirthdayViews();
        });
        updateBirthdayViews();
    }

    private void showBirthdayPicker() {
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
        Birthday birthday = settings.getBirthday();
        if (birthday != null) {
            month = birthday.getMonth() - 1;
            day = birthday.getDay();
            if (month == Calendar.FEBRUARY && day == 29 && !isLeapYear(year)) {
                year = nextLeapYear(year);
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (DatePicker picker, int selectedYear, int selectedMonth, int selectedDay) -> {
                    settings.setBirthday(selectedMonth + 1, selectedDay);
                    settings.save(sharedPreferences);
                    updateBirthdayViews();
                }, year, month, day);
        dialog.setTitle(R.string.birthday_picker_title);
        dialog.show();
    }

    private void updateBirthdayViews() {
        TextView birthdaySummary = findViewById(R.id.birthday_summary);
        Button birthdayPickerButton = findViewById(R.id.birthday_picker_button);
        Button birthdayClearButton = findViewById(R.id.birthday_clear_button);
        Birthday birthday = settings.getBirthday();
        if (birthday == null) {
            birthdaySummary.setText(R.string.birthday_not_set);
            birthdayPickerButton.setText(R.string.birthday_set_button);
            birthdayClearButton.setEnabled(false);
        } else {
            birthdaySummary.setText(getString(R.string.birthday_summary_set,
                    birthday.getDay(), birthday.getMonth()));
            birthdayPickerButton.setText(R.string.birthday_change_button);
            birthdayClearButton.setEnabled(true);
        }
    }

    private static int nextLeapYear(int year) {
        while (!isLeapYear(year)) {
            year++;
        }
        return year;
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }
}
