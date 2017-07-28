package com.yalin.wallpaper.demo.gl;


import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static final String PATH = "Path";
    private static final String FILE_NAME = "MPath";

    private static SharedPreferences sp;

    private static SharedPreferences getSp(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }


    public static String getPath(Context context) {
        return getSp(context).getString(PATH, "");
    }

    public static void setPath(Context context, String path) {
        getSp(context).edit().putString(PATH, path).commit();
    }

}
