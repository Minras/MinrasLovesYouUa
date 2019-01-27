package com.minras.lovesyou.ua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.minras.lovesyou.ua.Config.INTENT_DATA_KEY_SETTINGS;
import static com.minras.lovesyou.ua.Config.REQUEST_CODE_SETTINGS;
import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final private String PREFIX_ASK = "ask";
    final private String PREFIX_TELL = "tell";

    private int largeIconsTreshold = 2000;
    private String loveText = "";
    private TextView loveTextView;

    private String pkg;
    private SharedPreferences sharedPreferences;
    private Settings settings = new Settings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pkg = getPackageName();
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnTell = (ImageButton) findViewById(R.id.tellButton);
        btnTell.setOnClickListener(this);

        ImageButton btnAsk = (ImageButton) findViewById(R.id.askButton);
        btnAsk.setOnClickListener(this);

        settings.init(sharedPreferences);

        loveTextView = (TextView) findViewById(R.id.love_text_view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (displayMetrics.widthPixels > largeIconsTreshold || displayMetrics.heightPixels > largeIconsTreshold) {
            btnTell.setImageResource(R.drawable.unicorn_256_1049961);
            btnAsk.setImageResource(R.drawable.unicorn_256_1049947);
        }

        showMessage(PREFIX_TELL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askButton:
                showMessage(PREFIX_ASK);
                break;
            case R.id.tellButton:
                showMessage(PREFIX_TELL);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copyright:
                startActivity(new Intent(this, CopyrightActivity.class));
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(INTENT_DATA_KEY_SETTINGS, this.settings);
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loveTextView.setText(loveText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTINGS) {
            if (data.hasExtra(INTENT_DATA_KEY_SETTINGS)) {
                settings = (Settings) data.getExtras().get(INTENT_DATA_KEY_SETTINGS);
            }
        }
    }

    private boolean isMorning(final int hour) {
        return hour >= 5 && hour <= 11;
    }

    private boolean isDay(final int hour) {
        return hour >= 12 && hour <= 17;
    }

    private boolean isNight(final int hour) {
        return (hour == 23) || (hour >= 0 && hour <= 3);
    }

    private boolean isWinter(final int month) {
        return month == 0 || month == 1 || month == 11;
    }

    private List<String> getTimeBasedQuotes(final String prefix) {
        int resId;
        List<String> list = new ArrayList<>();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        if (isMorning(hour)) {
            resId = getResources().getIdentifier(prefix + "_array_morning", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
        } else if (isDay(hour)) {
            resId = getResources().getIdentifier(prefix + "_array_day", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
        } else if (isNight(hour)) {
            resId = getResources().getIdentifier(prefix + "_array_night", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
        }
        if (isWinter(month)) {
            resId = getResources().getIdentifier(prefix + "_array_winter", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
        }
        return list;
    }

    private void showMessage(final String prefix) {
        int resId = getResources().getIdentifier(prefix + "_array", "array", pkg);
        List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(resId)));
        if ("ol.zvereva@gmail.com".equals(settings.getAccountEmail()) || "andrey.shchurkov@gmail.com".equals(settings.getAccountEmail())) {
            resId = getResources().getIdentifier(prefix + "_array_personal", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
        }
        list.addAll(getTimeBasedQuotes(prefix));
        loveText = list.get(new Random().nextInt(list.size()));
        loveTextView.setText(loveText);
    }
}
