package com.minras.lovesyou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton btnRequest = (ImageButton) findViewById(R.id.requestLoveButton);
        btnRequest.setOnClickListener(this);

        ImageButton btnAsk = (ImageButton) findViewById(R.id.askAboutLoveButton);
        btnAsk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askAboutLoveButton:
                showMessage(R.array.ask_array);
                break;
            case R.id.requestLoveButton:
                showMessage(R.array.request_array);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copyright:
                showMessage(R.array.test_array);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMessage(int dataId) {
        String[] array = getResources().getStringArray(dataId);
        String text = array[new Random().nextInt(array.length)];
        if(null != toast) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }
}
