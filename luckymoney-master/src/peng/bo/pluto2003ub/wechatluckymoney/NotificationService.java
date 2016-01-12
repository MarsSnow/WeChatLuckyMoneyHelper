package peng.bo.pluto2003ub.wechatluckymoney;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

//通知服务
//若在其他界面，发现通知栏有微信红包消息，则直接执行相应的打开红包操作
@SuppressWarnings("NewApi")
public class NotificationService extends NotificationListenerService 
{	
	//接受到通知栏消息时
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

    //通知栏消息被移除时
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) 
    {

    }
    
    //获取通知栏消息
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
	
	//执行通知栏消息
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