package com.miscell.lucky;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NewApi")
public class NotificationService extends NotificationListenerService 
{	
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

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) 
    {

    }
    
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
                    	pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                    	
                    }
                    break;
                }
            }
        }
	}
}