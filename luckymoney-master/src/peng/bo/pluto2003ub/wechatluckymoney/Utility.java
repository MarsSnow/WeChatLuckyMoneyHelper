package peng.bo.pluto2003ub.wechatluckymoney;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class Utility 
{

//public static boolean isServiceRunning(Context mContext,String className) 
//{
//	boolean isRunning = false;
//	ActivityManager activityManager = (ActivityManager)
//	mContext.getSystemService(Context.ACTIVITY_SERVICE); 
//	List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
//	if (!(serviceList.size()>0)) 
//	{
//		return false;
//	}
//	
//	for (int i=0; i<serviceList.size(); i++) 
//	{
//		if (serviceList.get(i).service.getClassName().equals(className) == true) {
//			isRunning = true;
//		break;
//		}
//	}
//	
//	return isRunning
//}
	/** 
	 * 判断某个服务是否正在运行的方法 
	 *  
	 * @param mContext 
	 * @param serviceName 
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
	 * @return true代表正在运行，false代表服务没有正在运行 
	 */ 
	
	public boolean isServiceWork(Context mContext, String serviceName) {  
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}  
}