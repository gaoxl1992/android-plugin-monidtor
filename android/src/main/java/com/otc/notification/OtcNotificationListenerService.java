package com.otc.notification;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

/**
 * 通知监听服务
 * 监听系统所有通知，包括短信、微信、QQ等应用通知
 */
public class OtcNotificationListenerService extends NotificationListenerService {
    
    private static final String TAG = "OtcNotificationListener";
    private static OtcNotificationListenerService instance;
    private static NotificationCallback callback;
    
    public interface NotificationCallback {
        void onNotificationPosted(String packageName, String title, String text);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "NotificationListenerService created");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        Log.d(TAG, "NotificationListenerService destroyed");
    }
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        
        try {
            String packageName = sbn.getPackageName();
            Notification notification = sbn.getNotification();
            
            if (notification == null) {
                return;
            }
            
            Bundle extras = notification.extras;
            if (extras == null) {
                return;
            }
            
            // 获取通知标题
            String title = extras.getString(Notification.EXTRA_TITLE);
            if (title == null) {
                title = "";
            }
            
            // 获取通知内容
            CharSequence textSequence = extras.getCharSequence(Notification.EXTRA_TEXT);
            String text = textSequence != null ? textSequence.toString() : "";
            
            // 如果内容为空，尝试获取大文本
            if (TextUtils.isEmpty(text)) {
                CharSequence bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
                if (bigText != null) {
                    text = bigText.toString();
                }
            }
            
            Log.d(TAG, "Notification: pkg=" + packageName + ", title=" + title + ", text=" + text);
            
            // 回调到 JS
            if (callback != null && (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(text))) {
                callback.onNotificationPosted(packageName, title, text);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing notification", e);
        }
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG, "Notification removed: " + sbn.getPackageName());
    }
    
    /**
     * 设置通知回调
     */
    public static void setCallback(NotificationCallback cb) {
        callback = cb;
        Log.d(TAG, "Callback set: " + (cb != null));
    }
    
    /**
     * 移除通知回调
     */
    public static void removeCallback() {
        callback = null;
        Log.d(TAG, "Callback removed");
    }
    
    /**
     * 获取服务实例
     */
    public static OtcNotificationListenerService getInstance() {
        return instance;
    }
    
    /**
     * 检查服务是否正在运行
     */
    public static boolean isServiceRunning() {
        return instance != null;
    }
}
