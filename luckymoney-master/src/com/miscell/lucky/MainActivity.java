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
 *	��Activity
 */
public class MainActivity extends Activity {
	
    private static final Intent s_settingsIntent =new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private TextView m_accessibleLabel;																//��������															
    private TextView m_notificationLabel;															//֪ͨ����
    private TextView m_labelText;																    //˵���ı�

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
        
        
        ImageView imageView1 = (ImageView) findViewById(R.id.image_accessibility);				//��������
        ImageView imageView2 = (ImageView) findViewById(R.id.image_notification);				//֪ͨ����
        imageView1.setLayoutParams(lp);
        imageView2.setLayoutParams(lp);
        
        m_accessibleLabel = (TextView) findViewById(R.id.label_accessible);						//��������
        m_notificationLabel = (TextView) findViewById(R.id.label_notification);					//֪ͨ����
        m_labelText = (TextView) findViewById(R.id.label_text);									//˵���ı�

        ((TextView) findViewById(R.id.version_text)).setText("�汾:" + getVersionName());
        
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

    //��ȡ����汾��
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
    	
    	//��������
        boolean isAccessibilityEnabled = isAccessibleEnabled();
        m_accessibleLabel.setTextColor(isAccessibilityEnabled ? 0xFF009588 : Color.RED);
        m_accessibleLabel.setText(isAccessibleEnabled() ? "���������Ѵ�" : "��������δ��");
        m_labelText.setText(isAccessibilityEnabled ? "����~�����ȥ�����������ˣ��һ��Զ������������" : "��򿪿��ؿ�ʼ�����");

        //֪ͨ����
        if (Build.VERSION.SDK_INT >= 18) {
            boolean isNotificationEnabled = isNotificationEnabled();
            m_notificationLabel.setTextColor(isNotificationEnabled ? 0xFF009588 : Color.RED);
            m_notificationLabel.setText(isNotificationEnabled ? "����֪ͨ�Ѵ�" : "����֪ͨδ��");

            if (isAccessibilityEnabled && isNotificationEnabled) {
                m_labelText.setText("����~�����ȥ�����������ˣ��һ��Զ������������");
            } else {
                m_labelText.setText("����������ض��򿪿�ʼ�����");
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
        dialog.setTitle("��Ҫ!").setMessage("����Ҫ��\"�к��\"�ĸ�������ѡ�������΢�ź��")
                .setPositiveButton("��", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(s_settingsIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("ȡ��", null);
        dialog.show();
    }
}
