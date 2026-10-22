package com.minras.lovesyou.ua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity {

    private String loveText = "";
    private TextView loveTextView;

    private final Settings settings = new Settings();
    private QuoteSelector quoteSelector;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> settingsLauncher;
    private QuoteSelector.QuoteKind currentQuoteKind = QuoteSelector.QuoteKind.TELL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        settingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    settings.init(sharedPreferences);
                    showMessage(currentQuoteKind);
                });
        quoteSelector = new QuoteSelector(new AndroidQuoteCatalog(getResources()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton tellButton = findViewById(R.id.tellButton);
        tellButton.setOnClickListener(v -> showMessage(QuoteSelector.QuoteKind.TELL));

        ImageButton askButton = findViewById(R.id.askButton);
        askButton.setOnClickListener(v -> showMessage(QuoteSelector.QuoteKind.ASK));

        settings.init(sharedPreferences);

        loveTextView = findViewById(R.id.love_text_view);

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
            settingsLauncher.launch(intent);
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

    private void showMessage(final QuoteSelector.QuoteKind kind) {
        currentQuoteKind = kind;
        Calendar localDateTime = Calendar.getInstance();
        loveText = quoteSelector.select(kind, settings.arePersonalQuotesEnabled(),
                settings.getBirthday(), localDateTime);
        loveTextView.setText(loveText);
    }
}
