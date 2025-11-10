-keep class com.SharaSpot.core.model.** { *; }
-keep class com.SharaSpot.lib.managers.MessagingService { *; }
-dontwarn com.SharaSpot.payment.BR

# Retrofit, Gson, and Coroutines
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep public class * extends android.app.**
-keep public class com.google.j2objc.** { public *; }

-dontwarn com.google.j2objc.**
-dontwarn kotlinx.parcelize.**

# Koin Core & General
-keep class org.koin.** { *; }
-keep class org.koin.core.registry.** { *; }
-keep class org.koin.core.scope.** { *; }
-keep class org.koin.androidx.viewmodel.scope.** { *; }
-keep class org.koin.androidx.scope.** { *; }

# If using Koin KSP generated code (highly recommended for modern Koin)
-keep class org.koin.ksp.generated.** { *; }

# Remove Logs (more concise)
-assumenosideeffects class android.util.Log {
    public static *** *(...);
}

# Razorpay SDK ProGuard Rules
# Keep all Razorpay classes
-keep class com.razorpay.** { *; }

# Keep annotations
-keepattributes *Annotation*

# Keep Razorpay SDK resources
-keep class com.razorpay.RzpTokenReceiver { *; }
-keep class com.razorpay.Checkout { *; }
-keep class com.razorpay.PaymentResultListener { *; }
-keep class com.razorpay.PaymentResultWithDataListener { *; }
-keep class com.razorpay.PaymentData { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# OkHttp platform used by Razorpay
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson rules for Razorpay (if not already covered)
-keep class com.google.gson.stream.** { *; }

# Keep payment models used with Razorpay
-keep class com.powerly.core.model.payment.** { *; }
