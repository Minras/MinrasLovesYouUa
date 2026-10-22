package com.minras.lovesyou.ua;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Andrii Shchurkov on 10/7/2018.
 */
public class CopyrightActivity extends AppCompatActivity {

    private static final float UNICORN_WIDTH_FRACTION = 0.34f;
    private static final int TEXT_IMAGE_GAP_DP = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);
        SystemBarInsets.applyTopInset(findViewById(R.id.copyrightCoordinatorLayout));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView credits = findViewById(R.id.copyright_credits);
        credits.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop,
                oldRight, oldBottom) -> {
            if (right - left != oldRight - oldLeft) {
                credits.post(() -> positionUnicornBesideCredits(credits));
            }
        });
        credits.post(() -> positionUnicornBesideCredits(credits));
    }

    private void positionUnicornBesideCredits(TextView credits) {
        int availableWidth = credits.getWidth() - credits.getCompoundPaddingLeft()
                - credits.getCompoundPaddingRight();
        if (availableWidth <= 0) {
            return;
        }

        int unicornSize = Math.round(availableWidth * UNICORN_WIDTH_FRACTION);
        int lineHeight = Math.max(1, credits.getLineHeight());
        int indentedLineCount = Math.max(1, (unicornSize + lineHeight - 1) / lineHeight);
        int gap = Math.round(TEXT_IMAGE_GAP_DP * getResources().getDisplayMetrics().density);

        ImageView unicorn = findViewById(R.id.copyright_unicorn);
        FrameLayout.LayoutParams imageLayoutParams =
                (FrameLayout.LayoutParams) unicorn.getLayoutParams();
        imageLayoutParams.width = unicornSize;
        imageLayoutParams.height = unicornSize;
        imageLayoutParams.gravity = Gravity.TOP | Gravity.START;
        unicorn.setLayoutParams(imageLayoutParams);

        SpannableString text = new SpannableString(getString(R.string.copyright_credits));
        text.setSpan(new ImageFlowMarginSpan(unicornSize + gap, indentedLineCount),
                0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        credits.setText(text);
    }

    private static final class ImageFlowMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {

        private final int firstLineMargin;
        private final int indentedLineCount;

        private ImageFlowMarginSpan(int firstLineMargin, int indentedLineCount) {
            this.firstLineMargin = firstLineMargin;
            this.indentedLineCount = indentedLineCount;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return first ? firstLineMargin : 0;
        }

        @Override
        public int getLeadingMarginLineCount() {
            return indentedLineCount;
        }

        @Override
        public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int direction, int top,
                int baseline, int bottom, CharSequence text, int start, int end, boolean first,
                Layout layout) {
            // The unicorn is rendered by its sibling ImageView; this span only reserves text space.
        }
    }
}
