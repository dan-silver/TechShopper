package com.silver.dan.deals;

/**
 * Created by dan on 10/24/15.
 */

import android.view.View;

import static com.silver.dan.deals.AnimatorProxy.NEEDS_PROXY;
import static com.silver.dan.deals.AnimatorProxy.wrap;

public final class ViewHelper {
    private ViewHelper() {}

    public static float getTranslationY(View view) {
        return NEEDS_PROXY ? wrap(view).getTranslationY() : Honeycomb.getTranslationY(view);
    }

    public static void setTranslationY(View view, float translationY) {
        if (NEEDS_PROXY) {
            wrap(view).setTranslationY(translationY);
        } else {
            Honeycomb.setTranslationY(view, translationY);
        }
    }

    private static final class Honeycomb {

        static float getTranslationY(View view) {
            return view.getTranslationY();
        }

        static void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }

    }
}
