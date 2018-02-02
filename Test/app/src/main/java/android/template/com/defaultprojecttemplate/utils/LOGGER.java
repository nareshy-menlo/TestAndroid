package android.template.com.defaultprojecttemplate.utils;

import android.template.com.defaultprojecttemplate.BuildConfig;
import android.util.Log;

public class LOGGER {

    private static boolean isLogEnabled = BuildConfig.DEBUG;

    public static void error(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void info(String tag, String msg) {
        if (isLogEnabled) {
            Log.i(tag, msg);
        }
    }

    public static void verbose(String tag, String msg) {
        if (isLogEnabled) {
            Log.v(tag, msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (isLogEnabled) {
            Log.d(tag, msg);
        }
    }

    public static void warning(String tag, String msg) {
        if (isLogEnabled) {
            Log.w(tag, msg);
        }
    }

    public static void setLoggerEnabled(boolean logEnabled){
        isLogEnabled = logEnabled;
    }

    /**
     * Add a private constructor to hide the implicit public one.
     */
    private LOGGER() {
    }
}