# АГРЕССИВНАЯ ОБФУСЦИАЦИЯ ДЛЯ R8
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-repackageclasses 'com.secure.internal'
-dontusemixedcaseclassnames
-dontpreverify
-verbose

# УДАЛЕНИЕ ЛОГОВ
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
    public static *** e(...);
}

# ЗАЩИТА КРИТИЧЕСКИХ КЛАССОВ
-keep class com.analyzer.smsbeta.** { *; }
-keepclassmembers class com.analyzer.smsbeta.** { *; }

# ОБФУСЦИЯ ВСЕГО ОСТАЛЬНОГО
-keep class !com.analyzer.smsbeta.** { *; }
-keepclassmembers class !com.analyzer.smsbeta.** { *; }

# ЗАЩИТА КОМПОНЕНТОВ ANDROID
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application

# ЗАЩИТА WEBVIEW
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

# ЗАЩИТА REFLECTION
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# ЗАЩИТА NATIVE МЕТОДОВ
-keepclasseswithmembernames class * {
    native <methods>;
}

# УДАЛЕНИЕ ИНФОРМАЦИИ ОБ ИСХОДНОМ КОДЕ
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ОБРАБОТКА ДЕПРЕКИРОВАННЫХ МЕТОДОВ
-dontwarn android.telephony.**
-dontwarn okhttp3.**
-dontwarn okio.**
