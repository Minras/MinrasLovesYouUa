package com.minras.lovesyou.ua;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;

import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {

    final private String LOG_TAG = "DEBUG_TAG";

    private SharedPreferences sharedPreferences;
    private Settings settings;
    private ActivityResultLauncher<Intent> accountPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        settings = new Settings();
        settings.init(sharedPreferences);
        accountPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::handleAccountPickerResult);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Button btnSignin = (Button) findViewById(R.id.btn_signin);
        btnSignin.setOnClickListener(v -> signIn());

    }

    /**
     * Sign in with google account to get a registered email
     */
    private void signIn() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null, null,
                true, null, null, null, null);
        try {
            accountPickerLauncher.launch(googlePicker);
        } catch (ActivityNotFoundException ex) {
            // This device may not have Google Play Services installed.
            Log.w(LOG_TAG, "This device may not have Google Play Services installed");
        }
    }

    private void handleAccountPickerResult(ActivityResult result) {
        final String message;
        final String newEmail;
        final String errorMessage = "Не вдалося прив'язати аккаунт. Або ви його успішно відв'язали, тоді усе гаразд.";
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {
            newEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            message = newEmail == null
                    ? errorMessage
                    : "Аккаунт успішно прив'язано.";
        } else {
            newEmail = null;
            message = errorMessage;
        }
        settings.setAccountEmail(newEmail);
        settings.save(sharedPreferences);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
