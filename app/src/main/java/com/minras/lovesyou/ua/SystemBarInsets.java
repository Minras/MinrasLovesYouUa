package com.minras.lovesyou.ua;

import android.os.Build;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

final class SystemBarInsets {

    private SystemBarInsets() {
    }

    static void applyTopInset(View root) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return;
        }

        int initialTopPadding = root.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(view.getPaddingLeft(), initialTopPadding + insets.top,
                    view.getPaddingRight(), view.getPaddingBottom());
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
