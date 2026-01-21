# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Tencent IM SDK classes
-keep class com.tencent.imsdk.** { *; }
-dontwarn com.tencent.imsdk.**
