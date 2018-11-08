package com.minras.lovesyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int largeIconsTreshold = 2000;
    TextView loveTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askButton:
                showMessage(R.array.ask_array);
                break;
            case R.id.tellButton:
                showMessage(R.array.tell_array);
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

    private void showMessage(int dataId) {
        String[] array = getResources().getStringArray(dataId);
        String text = array[new Random().nextInt(array.length)];
        loveTextView.setText(text);
    }
}
