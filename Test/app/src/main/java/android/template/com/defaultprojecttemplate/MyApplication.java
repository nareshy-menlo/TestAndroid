package android.template.com.defaultprojecttemplate;

import android.app.Application;
import android.graphics.Typeface;
import android.template.com.defaultprojecttemplate.utils.FontUtils;

/**
 * Created by santhoshs on 1/23/2018.
 */

public class MyApplication extends Application {

    private static Preferences preferences;

    private static Typeface dogomaBold;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = Preferences.getInstance(this);

        initializeFonts();
    }

    private void initializeFonts() {
        dogomaBold    = FontUtils.getFontType(this, FontUtils.DOGAM_BOLD);
    }

    public static Typeface getDogomaBold() {
        return dogomaBold;
    }

    public static Preferences getPreferneces(){
        return preferences;
    }
}
