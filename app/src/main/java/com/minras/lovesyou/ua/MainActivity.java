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

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

import static com.minras.lovesyou.ua.Config.INTENT_DATA_KEY_SETTINGS;
import static com.minras.lovesyou.ua.Config.REQUEST_CODE_SETTINGS;
import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int largeIconsTreshold = 2000;
    private String loveText = "";
    private TextView loveTextView;

    private SharedPreferences sharedPreferences;
    private Settings settings = new Settings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        showMessage(R.array.tell_array, R.array.tell_array_personal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askButton:
                showMessage(R.array.ask_array, R.array.ask_array_personal);
                break;
            case R.id.tellButton:
                showMessage(R.array.tell_array, R.array.tell_array_personal);
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

    private void showMessage(int dataId, int personalDataId) {
        String[] array;
        if ("ol.zvereva@gmail.com".equals(settings.getAccountEmail()) || "andrey.shchurkov@gmail.com".equals(settings.getAccountEmail())) {
            array = ArrayUtils.addAll(getResources().getStringArray(dataId), getResources().getStringArray(personalDataId));
        } else {
            array = getResources().getStringArray(dataId);
        }
        loveText = array[new Random().nextInt(array.length)];
        loveTextView.setText(loveText);
    }
}
