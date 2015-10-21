package com.silver.dan.deals;

import android.content.Context;

/**
 * Created by dan on 10/21/15.
 */
public class utils {
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
