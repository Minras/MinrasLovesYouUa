package com.minras.lovesyou;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by Andrii Shchurkov on 10/7/2018.
 */
public class CopyrightActivity extends AppCompatActivity {

    final private static String ICON_COPYRIGHT = "The unicorn icons were made by " +
            "Freepik (www.flaticon.com/authors/freepik) " +
            "from www.flaticon.com. Thank you, guys!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TextView txt = (TextView) findViewById(R.id.text_copyright);
        txt.setText(ICON_COPYRIGHT);
        Linkify.addLinks(txt, Linkify.WEB_URLS);
    }
}
