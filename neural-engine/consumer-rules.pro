# Consumer ProGuard rules for Neural Engine library
# These rules will be applied to the consuming application

# Keep RFID SDK classes
-keep class com.zebra.** { *; }
-keep class com.symbol.** { *; }

# Keep RxJava classes
-keep class io.reactivex.rxjava3.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

# Keep payment SDK classes
-keep class com.razorpay.** { *; }

# Keep all public neural engine classes
-keep public class com.skynetbee.neuralengine.** { *; }

