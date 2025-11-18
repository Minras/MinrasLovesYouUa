package com.minras.lovesyou.ua;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import static com.minras.lovesyou.ua.Config.INTENT_DATA_KEY_SETTINGS;
import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    final private String LOG_TAG = "DEBUG_TAG";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 56171;

    private SharedPreferences sharedPreferences;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras == null || extras.get(INTENT_DATA_KEY_SETTINGS) == null) {
            throw new RuntimeException("Can't obtain settings from the calling activity");
        }
        settings = (Settings) extras.get(INTENT_DATA_KEY_SETTINGS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button btnSignin = (Button) findViewById(R.id.btn_signin);
        btnSignin.setOnClickListener(this);

        settings.init(sharedPreferences);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                signIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            final String message;
            final String newEmail;
            if (resultCode == RESULT_OK) {
                newEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                message = "Аккаунт успішно прив'язано.";
            } else {
                newEmail = null;
                message = "Не вдалося прив'язати аккаунт. Або ви його успішно відв'язали, тоді усе гаразд.";
            }
            settings.setAccountEmail(newEmail);
            settings.save(sharedPreferences);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra(INTENT_DATA_KEY_SETTINGS, this.settings);
        setResult(RESULT_OK, data);
        super.finish();
    }

    /**
     * Sign in with google account to get a registered email
     */
    private void signIn() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null, null,
                true, null, null, null, null);
        try {
            startActivityForResult(googlePicker, REQUEST_CODE_ASK_PERMISSIONS);
        } catch (ActivityNotFoundException ex) {
            // This device may not have Google Play Services installed.
            Log.w(LOG_TAG, "This device may not have Google Play Services installed");
        }
    }
}
