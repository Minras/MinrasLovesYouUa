package com.minras.lovesyou.ua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

//        int largeIconsTreshold = 2000;
//        Looks like the code below tried to make a bigger picture for bigger screens, but it was too big actually
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        if (displayMetrics.widthPixels > largeIconsTreshold || displayMetrics.heightPixels > largeIconsTreshold) {
//            btnTell.setImageResource(R.drawable.unicorn_256_1049961);
//            btnAsk.setImageResource(R.drawable.unicorn_256_1049947);
//        }

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
        super.onActivityResult(requestCode, resultCode, data);
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

    private Suffix calculateSeasonName(final int month) {
        switch (month) {
            case 0:
            case 1:
            case 11:
                return Suffix.WINTER;
            case 2:
            case 3:
            case 4:
                return Suffix.SPRING;
            case 5:
            case 6:
            case 7:
                return Suffix.SUMMER;
            case 8:
            case 9:
            case 10:
                return Suffix.AUTUMN;
            default:
                return null;
        }
    }

    private List<String> getTimeBasedQuotes(final String prefix) {
        int resId;
        List<Suffix> suffixes = new ArrayList<>();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        if (isMorning(hour)) {
            suffixes.add(Suffix.MORNING);
        } else if (isDay(hour)) {
            suffixes.add(Suffix.DAY);
        } else if (isNight(hour)) {
            suffixes.add(Suffix.NIGHT);
        }
        final Suffix season = calculateSeasonName(month);
        if (null != season) {
            suffixes.add(season);
        }

        if ((10 == month && 31 == day) || (11 == month && 1 == day)) {
            // three times more probability to see the quote
            suffixes.add(Suffix.HALLOWEEN);
            suffixes.add(Suffix.HALLOWEEN);
            suffixes.add(Suffix.HALLOWEEN);
        }
        if (12 == month && day >=20 && day <= 27) {
            // three times more probability to see the quote
            suffixes.add(Suffix.XMAS);
            suffixes.add(Suffix.XMAS);
            suffixes.add(Suffix.XMAS);
        }
        if ((1 == month && day >=1 && day <= 7) || (12 == month && day > 24)) {
            // three times more probability to see the quote
            suffixes.add(Suffix.NEWYEAR);
            suffixes.add(Suffix.NEWYEAR);
            suffixes.add(Suffix.NEWYEAR);
        }

        List<String> quotes = new ArrayList<>();
        for (Suffix suffix : suffixes) {
            resId = getResources().getIdentifier(prefix + "_array_" + suffix.getValue(), "array", pkg);
            quotes.addAll(Arrays.asList(getResources().getStringArray(resId)));
        }
        return quotes;
    }

    private void showMessage(final String prefix) {
        int resId = getResources().getIdentifier(prefix + "_array", "array", pkg);
        List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(resId)));
        if ("ol.zvereva@gmail.com".equals(settings.getAccountEmail()) || "andrey.shchurkov@gmail.com".equals(settings.getAccountEmail())) {
            resId = getResources().getIdentifier(prefix + "_array_personal", "array", pkg);
            list.addAll(Arrays.asList(getResources().getStringArray(resId)));
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            if (day == 8 && month == 1) {
                resId = getResources().getIdentifier(prefix + "_array_personal_birthday", "array", pkg);
                list.addAll(Arrays.asList(getResources().getStringArray(resId)));
            }
        }
        list.addAll(getTimeBasedQuotes(prefix));
        loveText = list.get(new Random().nextInt(list.size()));
        loveTextView.setText(loveText);
    }

    public enum Suffix {

        MORNING("morning"),
        DAY("day"),
        NIGHT("night"),

        WINTER("winter"),
        SPRING("spring"),
        SUMMER("summer"),
        AUTUMN("autumn"),

        HALLOWEEN("halloween"),
        NEWYEAR("newyear"),
        XMAS("xmas");

        private String value;

        Suffix(String suffix) {
            this.value = suffix;
        }

        public String getValue() {
            return value;
        }
    }
}
