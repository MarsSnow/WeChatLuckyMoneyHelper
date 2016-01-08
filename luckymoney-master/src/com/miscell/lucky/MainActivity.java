package com.miscell.lucky;

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

import java.util.List;

//
/**
 * @author pengbo
 *	主Activity
 */
public class MainActivity extends Activity {
	
    private static final Intent s_settingsIntent =new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private TextView m_accessibleLabel;																//辅助功能															
    private TextView m_notificationLabel;															//通知功能
    private TextView m_labelText;																    //说明文本

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        final int screenWidth = metrics.widthPixels;

        int width = (int) (screenWidth - (density * 12 + .5f) * 2);
        int height = (int) (366.f * width / 1080);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        
        
        ImageView imageView1 = (ImageView) findViewById(R.id.image_accessibility);				//辅助功能
        ImageView imageView2 = (ImageView) findViewById(R.id.image_notification);				//通知功能
        imageView1.setLayoutParams(lp);
        imageView2.setLayoutParams(lp);
        
        m_accessibleLabel = (TextView) findViewById(R.id.label_accessible);						//辅助功能
        m_notificationLabel = (TextView) findViewById(R.id.label_notification);					//通知功能
        m_labelText = (TextView) findViewById(R.id.label_text);									//说明文本

        ((TextView) findViewById(R.id.version_text)).setText("版本:" + getVersionName());
        
        if (Build.VERSION.SDK_INT >= 18) {
        	
            imageView2.setVisibility(View.VISIBLE);
            m_notificationLabel.setVisibility(View.VISIBLE);
            findViewById(R.id.button_notification).setVisibility(View.VISIBLE);
        } else {
        	
            imageView2.setVisibility(View.GONE);
            m_notificationLabel.setVisibility(View.GONE);
            findViewById(R.id.button_notification).setVisibility(View.GONE);
        }

   

//        imageView1.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("test", "# fired");
//                unlockScreen();
//
//            }
//        }, 5000L);
    }

    //获取程序版本号
    private String getVersionName() {
        String versionName = "";

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }


    @Override
    protected void onResume() {
        super.onResume();
        changeLabelStatus();
    }

    private void changeLabelStatus() {
    	
    	//辅助功能
        boolean isAccessibilityEnabled = isAccessibleEnabled();
        m_accessibleLabel.setTextColor(isAccessibilityEnabled ? 0xFF009588 : Color.RED);
        m_accessibleLabel.setText(isAccessibleEnabled() ? "辅助功能已打开" : "辅助功能未打开");
        m_labelText.setText(isAccessibilityEnabled ? "好了~你可以去做其他事情了，我会自动给你抢红包的" : "请打开开关开始抢红包");

        //通知功能
        if (Build.VERSION.SDK_INT >= 18) {
            boolean isNotificationEnabled = isNotificationEnabled();
            m_notificationLabel.setTextColor(isNotificationEnabled ? 0xFF009588 : Color.RED);
            m_notificationLabel.setText(isNotificationEnabled ? "接收通知已打开" : "接收通知未打开");

            if (isAccessibilityEnabled && isNotificationEnabled) {
                m_labelText.setText("好了~你可以去做其他事情了，我会自动给你抢红包的");
            } else {
                m_labelText.setText("请把两个开关都打开开始抢红包");
            }
        }
    }

    public void onNotificationEnableButtonClicked(View view) {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    public void onSettingsClicked(View view) {
        startActivity(s_settingsIntent);
    }

    private boolean isAccessibleEnabled() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName() + "/.MonitorService")) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotificationEnabled() {
        ContentResolver contentResolver = getContentResolver();
        String enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

        if (!TextUtils.isEmpty(enabledListeners)) {
            return enabledListeners.contains(getPackageName() + "/" + getPackageName() + ".NotificationService");
        } else {
            return false;
        }
    }

    private void showEnableAccessibilityDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle("重要!").setMessage("您需要打开\"有红包\"的辅助功能选项才能抢微信红包")
                .setPositiveButton("打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(s_settingsIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null);
        dialog.show();
    }
}
