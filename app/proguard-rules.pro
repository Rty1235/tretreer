# Агрессивная оптимизация и обфускация
-optimizationpasses 10
-allowaccessmodification
-overloadaggressively
-repackageclasses 'com.obfuscated.internal'
-useuniqueclassmembernames
-flattenpackagehierarchy
-mergeinterfacesaggressively

# Удаление всей отладочной информации
-keepattributes Exceptions,InnerClasses,Signature,*Annotation*
-renamesourcefileattribute SourceFile
-keepparameternames

# Динамическая загрузка классов
-keep class * extends java.lang.ClassLoader
-keep class * implements java.lang.reflect.InvocationHandler

# Специальная обфускация для критичных классов
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Сохранение только минимально необходимых компонентов
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View

# Улучшенная защита WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Обфускация строковых констант
-adaptclassstrings
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

# Ложные контрольные точки
-dontnote com.google.**
-dontwarn com.google.**
-keep class com.google.** { *; }

# Защита нативных методов
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Удаление логов
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Дополнительные меры защиты
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    public void *(android.view.View);
}

# Защита от динамического анализа
-keep class * extends java.lang.Exception
-keep class * extends java.lang.Error

# Специфичные правила для вашего приложения
-keep class com.analyzer.smsbeta.** { *; }
-keepclassmembers class com.analyzer.smsbeta.** {
    *;
}

# Защита ресурсов
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Дополнительная обфускация строк
-assumenosideeffects class java.lang.String {
    public static *** valueOf(...);
    public *** toString(...);
    public *** substring(...);
    public *** replace(...);
}

# Защита от hooking-фреймворков
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
