package com.MirageStudios.NeonBackgroundView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.MirageStudios.library.NeonBackgroundLayout;

public class MainActivity extends Activity {

    private static float dpToPx(float valueDp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDp,
                displayMetrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NeonBackgroundLayout neonBackgroundLayout = findViewById(R.id.neon_background_layout);
        neonBackgroundLayout.setBackgroundPadding(
                dpToPx(1, this),
                dpToPx(1, this),
                dpToPx(1, this),
                dpToPx(1, this)
        );
        neonBackgroundLayout.setInnerBackgroundPadding(dpToPx(12, this));
        neonBackgroundLayout.setCornerRadius(dpToPx(24, this));
        neonBackgroundLayout.setStrokeWidth(dpToPx(4, this));
        neonBackgroundLayout.setShadowMultiplier(2);
        neonBackgroundLayout.setStrokeColor(Color.parseColor("#FFB300"));
        neonBackgroundLayout.setShadowColor(Color.parseColor("#FFB300"));
        neonBackgroundLayout.setInnerBackgroundColor(Color.parseColor("#48000000"));
    }
}