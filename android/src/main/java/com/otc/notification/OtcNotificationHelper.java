package com.otc.notification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * 通知监听辅助类
 * 提供简单的静态方法供 plus.android 调用
 */
public class OtcNotificationHelper {
    
    private static final String TAG = "OtcNotificationHelper";
    
    /**
     * 检查是否有通知监听权限
     */
    public static boolean checkPermission(Context context) {
        try {
            String packageName = context.getPackageName();
            String flat = Settings.Secure.getString(
                context.getContentResolver(),
                "enabled_notification_listeners"
            );
            
            if (!TextUtils.isEmpty(flat)) {
                String[] names = flat.split(":");
                for (String name : names) {
                    ComponentName cn = ComponentName.unflattenFromString(name);
                    if (cn != null && TextUtils.equals(packageName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 打开通知监听设置页面
     */
    public static void openSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 检查服务是否运行
     */
    public static boolean isServiceRunning() {
        return OtcNotificationListenerService.isServiceRunning();
    }
    
    /**
     * 设置消息监听器
     */
    public static void setCallback(OtcNotificationListenerService.NotificationCallback callback) {
        OtcNotificationListenerService.setCallback(callback);
    }
    
    /**
     * 移除消息监听器
     */
    public static void removeCallback() {
        OtcNotificationListenerService.removeCallback();
    }
}
