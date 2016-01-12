package peng.bo.pluto2003ub.wechatluckymoney;

import android.app.Application;
import com.flurry.android.FlurryAgent;

//应用
public class LuckyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //FlurryAgent.init(this, "37T6NSWDQZ28FNJSWYBB");
    }
}