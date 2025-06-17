# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep RFID SDK classes
-keep class com.zebra.** { *; }
-keep class com.symbol.** { *; }

# Keep RxJava classes
-keep class io.reactivex.rxjava3.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

# Keep payment SDK classes
-keep class com.razorpay.** { *; }

# Keep all public classes and methods in the neural engine
-keep public class com.skynetbee.neuralengine.** { *; }

