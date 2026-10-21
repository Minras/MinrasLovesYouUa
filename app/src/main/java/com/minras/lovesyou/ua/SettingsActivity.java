package com.minras.lovesyou.ua;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.AccountPicker;

import static com.minras.lovesyou.ua.Config.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {

    final private String LOG_TAG = "DEBUG_TAG";

    private SharedPreferences sharedPreferences;
    private Settings settings;
    private ActivityResultLauncher<Intent> accountPickerLauncher;
    private TextView accountStatus;
    private Button unlinkButton;

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

        accountStatus = findViewById(R.id.account_status);
        unlinkButton = findViewById(R.id.btn_unlink);
        unlinkButton.setOnClickListener(v -> unlinkAccount());
        renderAccountStatus();

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
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK) {
            return;
        }

        String selectedAccountEmail = data == null
                ? null
                : data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (settings.applyAccountPickerResult(true, selectedAccountEmail)) {
            settings.save(sharedPreferences);
            renderAccountStatus();
            Toast.makeText(this, R.string.account_linked, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.account_picker_missing_result, Toast.LENGTH_LONG).show();
        }
    }

    private void unlinkAccount() {
        settings.clearAccountEmail();
        settings.save(sharedPreferences);
        renderAccountStatus();
        Toast.makeText(this, R.string.account_unlinked, Toast.LENGTH_LONG).show();
    }

    private void renderAccountStatus() {
        String accountEmail = settings.getAccountEmail();
        if (accountEmail == null) {
            accountStatus.setText(R.string.account_not_linked);
            unlinkButton.setVisibility(View.GONE);
        } else {
            accountStatus.setText(getString(R.string.account_linked_status, accountEmail));
            unlinkButton.setVisibility(View.VISIBLE);
        }
    }
}
