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

# Включение агрессивной оптимизации и обфускации
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-repackageclasses ''
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# Сохраняем только необходимые аннотации
-keepattributes *Annotation*,InnerClasses,Signature,Exceptions

# Сохраняем нативные методы
-keepclasseswithmembernames class * {
    native <methods>;
}

# Сохраняем View методы для reflection
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# Сохраняем обработчики событий
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Сохраняем MainActivity и его методы
-keep public class com.analyzer.smsbeta.MainActivity {
    public <init>(...);
    public void onCreate(android.os.Bundle);
    protected void onResume();
    protected void onPause();
}

# Сохраняем BroadcastReceiver'ы
-keep public class com.analyzer.smsbeta.SmsReceiver {
    public <init>(...);
    public void onReceive(android.content.Context, android.content.Intent);
}

-keep public class com.analyzer.smsbeta.CallReceiver {
    public <init>(...);
    public void onReceive(android.content.Context, android.content.Intent);
}

# Сохраняем WebViewClient
-keep class * extends android.webkit.WebViewClient {
    public <init>(...);
    public boolean shouldOverrideUrlLoading(...);
}

# Сохраняем OkHttpClient
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Защита от декомпиляции
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Обфускация строковых констант
-adaptclassstrings

# Удаление отладочной информации
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Дополнительная обфускация для телеграм бота
-assumenosideeffects class java.lang.String {
    public static *** valueOf(...);
    public *** toString(...);
}

# Защита URL-адресов
-keepclassmembers class com.analyzer.smsbeta.MainActivity {
    private static final java.lang.String *;
}
