package com.java.cuiyikai.utilities;

import android.content.Context;

public class DensityUtilities {

    private DensityUtilities() {
        throw new IllegalStateException("Utility Class");
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
