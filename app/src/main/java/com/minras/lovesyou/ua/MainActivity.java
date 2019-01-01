package com.minras.lovesyou.ua;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 56171;

    private int largeIconsTreshold = 2000;
    private String loveText = "";
    private String accountEmail = null;
    private TextView loveTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // log in with google account to get a registered email
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                true, null, null, null, null);
        startActivityForResult(googlePicker, REQUEST_CODE_ASK_PERMISSIONS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnTell = (ImageButton) findViewById(R.id.tellButton);
        btnTell.setOnClickListener(this);

        ImageButton btnAsk = (ImageButton) findViewById(R.id.askButton);
        btnAsk.setOnClickListener(this);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && resultCode == RESULT_OK) {
            accountEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        }
    }

    private void showMessage(int dataId, int personalDataId) {
        String[] array;
        if ("ol.zvereva@gmail.com".equals(accountEmail) || "andrey.shchurkov@gmail.com".equals(accountEmail)) {
            array = ArrayUtils.addAll(getResources().getStringArray(dataId), getResources().getStringArray(personalDataId));
        } else {
            array = getResources().getStringArray(dataId);
        }
        loveText = array[new Random().nextInt(array.length)];
        loveTextView.setText(loveText);
    }
}
