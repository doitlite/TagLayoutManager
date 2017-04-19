package com.doitlite.taglayoutmanager.example.utils;

import android.content.Context;

/**
 * Description:
 * Date: 2017-04-18 19:48
 * Author: chenzc
 */
public class Utils {

    public static int dp2px(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

}
