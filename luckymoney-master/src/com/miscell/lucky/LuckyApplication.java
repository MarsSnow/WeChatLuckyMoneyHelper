package com.miscell.lucky;

import android.app.Application;
import com.flurry.android.FlurryAgent;

//”¶”√
public class LuckyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //FlurryAgent.init(this, "37T6NSWDQZ28FNJSWYBB");
    }
}