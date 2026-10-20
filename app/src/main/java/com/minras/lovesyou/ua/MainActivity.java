package com.minras.lovesyou.ua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import static com.minras.lovesyou.ua.Config.INTENT_DATA_KEY_SETTINGS;
import static com.minras.lovesyou.ua.Config.REQUEST_CODE_SETTINGS;
import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity {

    private String loveText = "";
    private TextView loveTextView;

    private Settings settings = new Settings();
    private QuoteSelector quoteSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        quoteSelector = new QuoteSelector(new AndroidQuoteCatalog(getResources()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton tellButton = findViewById(R.id.tellButton);
        tellButton.setOnClickListener(v -> showMessage(QuoteSelector.QuoteKind.TELL));

        ImageButton askButton = findViewById(R.id.askButton);
        askButton.setOnClickListener(v -> showMessage(QuoteSelector.QuoteKind.ASK));

        settings.init(sharedPreferences);

        loveTextView = findViewById(R.id.love_text_view);

//        int largeIconsTreshold = 2000;
//        Looks like the code below tried to make a bigger picture for bigger screens, but it was too big actually
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        if (displayMetrics.widthPixels > largeIconsTreshold || displayMetrics.heightPixels > largeIconsTreshold) {
//            btnTell.setImageResource(R.drawable.unicorn_256_1049961);
//            btnAsk.setImageResource(R.drawable.unicorn_256_1049947);
//        }

        showMessage(QuoteSelector.QuoteKind.TELL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_copyright) {
            startActivity(new Intent(this, CopyrightActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(INTENT_DATA_KEY_SETTINGS, this.settings);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
            return true;
        } else {
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loveTextView.setText(loveText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTINGS && data != null) {
            Settings receivedSettings = (Settings) data.getSerializableExtra(INTENT_DATA_KEY_SETTINGS);
            if (receivedSettings != null) {
                settings = receivedSettings;
            }
        }
    }

    private void showMessage(final QuoteSelector.QuoteKind kind) {
        loveText = quoteSelector.select(kind, settings.getAccountEmail(), Calendar.getInstance());
        loveTextView.setText(loveText);
    }
}
