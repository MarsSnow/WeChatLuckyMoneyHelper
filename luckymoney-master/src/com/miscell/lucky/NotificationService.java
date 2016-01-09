package com.miscell.lucky;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

//通知服务
//作用：若有微信红包通知，则执行抢红包操作
@SuppressWarnings("NewApi")
public class NotificationService extends NotificationListenerService 
{	
	//当接收到通知时
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) 
    {
        Notification notification = sbn.getNotification();
        
        if (null != notification) 
        {
            Bundle bundle = notification.extras;
            
            if (null != bundle) 
            {
                List<String> infos = GetNotificationInfos(bundle);

                PerformNotificationOperation(infos, notification);
            }
        }
    }

    //当通知被移除时
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) 
    {

    }
    
    //获取通知栏的数据
	private List<String> GetNotificationInfos(Bundle bundle)
	{
		 List<String> strList = new ArrayList<String>();
         String titleStr = bundle.getString("android.title");
         
         if (!TextUtils.isEmpty(titleStr))
         {
         	strList.add(titleStr);
         } 

         String testStr = bundle.getString("android.text");
         if (!TextUtils.isEmpty(testStr)){
         	strList.add(testStr);
         } 
         
         return strList;
	}
	
	//执行通知栏的操作
	private void PerformNotificationOperation(List<String> strList, Notification notification)
	{
		if (strList.size() > 0) 
        {
            for (String str : strList) 
            {
                if (!TextUtils.isEmpty(str) && str.contains("[微信红包]")) 
                {
                    final PendingIntent pendingIntent = notification.contentIntent;
                    try {
                    	//执行通知栏的抢红包操作
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                    	
                    }
                    break;
                }
            }
        }
	}
}
