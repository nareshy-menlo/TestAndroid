package android.template.com.defaultprojecttemplate.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {
    //Declare custom fonts here
    public static final String DOGAM_BOLD = "fonts/DogmaBold.ttf";

    public static Typeface getFontType(Context context, String fontType){
        return Typeface.createFromAsset(context.getAssets(), fontType);
    }
}