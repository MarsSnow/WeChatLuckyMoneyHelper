package peng.bo.pluto2003ub.wechatluckymoney;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//主Activity
public class MainActivity extends Activity 
{
    private static final Intent s_settingsIntent =new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private TextView m_accessibleLabel;																														
    private TextView m_notificationLabel;															
    private TextView m_labelText;																    

    //创建时
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        final int screenWidth = metrics.widthPixels;

        int width = (int) (screenWidth - (density * 12 + .5f) * 2);
        int height = (int) (366.f * width / 1080);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
         
        m_accessibleLabel = (TextView) findViewById(R.id.label_accessible);						
        m_notificationLabel = (TextView) findViewById(R.id.label_notification);					
        m_labelText = (TextView) findViewById(R.id.label_text);									

        ((TextView) findViewById(R.id.version_text)).setText("版本号:" + getVersionName());
        
        if (Build.VERSION.SDK_INT >= 18) {
            m_notificationLabel.setVisibility(View.VISIBLE);
            findViewById(R.id.button_notification).setVisibility(View.VISIBLE);
        } else {
            m_notificationLabel.setVisibility(View.GONE);
            findViewById(R.id.button_notification).setVisibility(View.GONE);
        }

        Toast.makeText(getApplicationContext(), "抢红包啦！",
          	     Toast.LENGTH_SHORT).show();
    }

    //从其他界面返回时调用
    @Override
    protected void onResume() 
    {
        super.onResume();
        changeLabelStatus();
    }
    
    //点击设置通知栏按钮时(xml反射调用)
    public void onNotificationEnableButtonClicked(View view) {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        Toast.makeText(getApplicationContext(), "点击通知栏",
        	     Toast.LENGTH_SHORT).show();
    }

    //设置按钮点击时（xml反射调用）
    public void onSettingsClicked(View view) {
        startActivity(s_settingsIntent);
        Toast.makeText(getApplicationContext(), "点击辅助服务",
       	     Toast.LENGTH_SHORT).show();
    }
    //改变Label的状态
    private void changeLabelStatus() 
    {
    	//辅助功能
        boolean isAccessibilityEnabled = isAccessibleEnabled();
        m_accessibleLabel.setTextColor(isAccessibilityEnabled ? 0xFF009588 : Color.RED);
        m_accessibleLabel.setText(isAccessibleEnabled() ? "辅助功能已成功打开" : "辅助功能未打开");
        m_labelText.setText(isAccessibilityEnabled ? "好了~你可以去做其他事情了，我会自动给你抢红包的" : "请打开开关开始抢红包");

        //通知功能
        if (Build.VERSION.SDK_INT >= 18) {
            boolean isNotificationEnabled = isNotificationEnabled();
            m_notificationLabel.setTextColor(isNotificationEnabled ? 0xFF009588 : Color.RED);
            m_notificationLabel.setText(isNotificationEnabled ? "接收通知已成功打开" : "接收通知未打开");

            if (isAccessibilityEnabled && isNotificationEnabled) {
                m_labelText.setText("作者：pluto2003ub");
            } else {
                m_labelText.setText("请把两个开关都打开开始抢红包");
            }
        }
    }
    
    //获得版本名字
    private String getVersionName() 
    {
        String versionName = "";

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    //辅助功能是否已经打开
    private boolean isAccessibleEnabled() 
    {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName() + "/.MonitorService")) {
                return true;
            }
        }
        return false;
    }

    //通知功能是否打开
    private boolean isNotificationEnabled() {
        ContentResolver contentResolver = getContentResolver();
        String enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

        if (!TextUtils.isEmpty(enabledListeners)) {
            return enabledListeners.contains(getPackageName() + "/" + getPackageName() + ".NotificationService");
        } else {
            return false;
        }
    }
}