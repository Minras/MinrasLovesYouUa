package com.minras.lovesyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnRequest;
    private ImageButton btnAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = (ImageButton) findViewById(R.id.requestLoveButton);
        btnRequest.setOnClickListener(this);

        btnAsk = (ImageButton) findViewById(R.id.askAboutLoveButton);
        btnAsk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askAboutLoveButton:
                showMessage();
                break;
            case R.id.requestLoveButton:
                showMessage();
                break;
        }
    }

    private void showMessage() {
        Toast toast = Toast.makeText(getApplicationContext(), "sample message", Toast.LENGTH_LONG);
        toast.show();
    }
}
