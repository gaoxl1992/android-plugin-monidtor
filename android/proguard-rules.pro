# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep UniModule classes
-keep class com.otc.notification.** { *; }
-keep class io.dcloud.feature.uniapp.** { *; }

# Keep notification service
-keep public class * extends android.service.notification.NotificationListenerService

# Keep annotations
-keepattributes *Annotation*
