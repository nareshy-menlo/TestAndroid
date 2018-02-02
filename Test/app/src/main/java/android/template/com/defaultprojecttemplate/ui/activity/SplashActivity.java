package android.template.com.defaultprojecttemplate.ui.activity;

import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_SCREEN_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delayedNextActivity();
    }

    public void delayedNextActivity () {
        // Keep the splash screen for few seconds and then move to the next
        // activity by killing the existing activity
        Handler splashScreenHandler = new Handler();
        //place the intent for next activity
        splashScreenHandler.postDelayed(this::finish, SPLASH_SCREEN_DURATION);
    }


    @Override
    public void onBackPressed () {
    }
}