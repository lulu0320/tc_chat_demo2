# Consumer proguard rules for messaging-sdk

# Keep Tencent IM SDK classes
-keep class com.tencent.imsdk.** { *; }
-dontwarn com.tencent.imsdk.**
