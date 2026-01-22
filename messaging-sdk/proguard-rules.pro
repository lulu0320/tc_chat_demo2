# Add project specific ProGuard rules here.

# Keep Tencent IM SDK classes
-keep class com.tencent.imsdk.** { *; }
-dontwarn com.tencent.imsdk.**

# Keep public API
-keep public class com.example.messaging.** { *; }
