package peng.bo.pluto2003ub.wechatluckymoney;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//监听器服务
//作用：监听屏幕红包信息，并进行处理
public class MonitorService extends AccessibilityService 
{	
    private boolean m_isClicked;
 
    //接受到辅助功能的事件时
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) 
    {
        final int eventType = event.getEventType();												

        //新的显示事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) 
        {
        	OnNotificationChanged(event);      
        }
        
        //显示事件窗口变化
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
        {
        	OnWindowsChanged(event);
        }
    }

    //程序被打断时
    @Override
    public void onInterrupt() 
    {

    }
    
    //通知栏消息变化
    private void OnNotificationChanged(AccessibilityEvent event)
    {
        UnlockScreen();																	
        
        m_isClicked = false;											

        /**
         * for API >= 18, we use NotificationListenerService to detect the notifications
         * below API_18 we use AccessibilityService to detect
         */

        if (Build.VERSION.SDK_INT < 18) 
        {
            Notification notification = (Notification) event.getParcelableData();
            List<String> textList = getText(notification);
            if (null != textList && textList.size() > 0) {
                for (String text : textList) {
                    if (!TextUtils.isEmpty(text) && text.contains("[微信红包]")) {
                        final PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                        }
                        break;
                    }
                }
            }
        }  
        
    }
    
    //窗口发生变化时
    private void OnWindowsChanged(AccessibilityEvent event)
    {
        String clazzName = event.getClassName().toString();													
        
        if (clazzName.equals("com.tencent.mm.ui.LauncherUI")) 
        {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (null != nodeInfo) {
            	 List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
                if (null != list && list.size() > 0) {
                    AccessibilityNodeInfo node = list.get(list.size() - 1);
                    if (node.isClickable()) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        AccessibilityNodeInfo parentNode = node;
                        for (int i = 0; i < 5; i++) {
                            if (null != parentNode) {
                                parentNode = parentNode.getParent();
                                if (null != parentNode && parentNode.isClickable() && !m_isClicked) {
                                    parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    m_isClicked = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //若类名和微信领取红包二级界面一致
        if (clazzName.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (null != nodeInfo) {
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
                for (AccessibilityNodeInfo node : list) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }
    
    //解锁屏幕
    private void UnlockScreen() 
    {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

        wakeLock.acquire();
    }

    //获得文本
    private List<String> getText(Notification notification) {
        if (null == notification) return null;

        RemoteViews views = notification.bigContentView;
        if (views == null) views = notification.contentView;
        if (views == null) return null;

        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<String>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);

            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;

                // View ID
                parcel.readInt();

                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();

                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
        }

        return text;
    }

}