package com.minras.lovesyou.ua;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by Andrii Shchurkov on 10/7/2018.
 */
public class CopyrightActivity extends AppCompatActivity {

    final private static String ICON_COPYRIGHT = "За іконки з єдинорогами та хмаринку подяка " +
            "Freepik та Vecteezy з www.freepik.com. Щиро вдячний, хлопці!";

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
