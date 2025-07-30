# АГРЕССИВНАЯ ОБФУСЦИЯ (ANTI-REVERSE ENGINEERING)
-optimizationpasses 10
-allowaccessmodification
-overloadaggressively
-repackageclasses 'com.secure.internal'
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/propagation/*
-dontusemixedcaseclassnames
-dontpreverify
-verbose
-flattenpackagehierarchy
-adaptclassstrings

# УДАЛЕНИЕ ЛОГОВ И ДЕБАГ-ИНФОРМАЦИИ
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
    public static *** e(...);
}

# ЗАЩИТА КРИТИЧЕСКИХ КЛАССОВ (частичное сохранение структуры)
-keep class com.analyzer.smsbeta.** { *; }
-keepclassmembers class com.analyzer.smsbeta.** { *; }

# ОБФУСЦИЯ ВСЕХ ОСТАЛЬНЫХ КЛАССОВ
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

# ЗАЩИТА REFLECTION (GSON, Retrofit и т.д.)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# ШИФРОВАНИЕ СТРОК (используйте с осторожностью)
-encryptstrings !com.analyzer.smsbeta.**,!android.**,!com.android.**
-classobfuscationdictionary 'dict.txt' 
-packageobfuscationdictionary 'dict.txt'

# ЗАЩИТА NATIVE МЕТОДОВ
-keepclasseswithmembernames class * {
    native <methods>;
}

# УДАЛЕНИЕ ИНФОРМАЦИИ О ИСХОДНОМ КОДЕ
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ЛОЖНЫЕ ССЫЛКИ ДЛЯ ЗАПУТЫВАНИЯ АНАЛИЗА
-dontnote com.google.**
-dontwarn okhttp3.**
-dontwarn okio.**
