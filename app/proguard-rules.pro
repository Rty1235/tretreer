# БАЗОВЫЕ НАСТРОЙКИ
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify
-verbose

# ОБРАБОТКА ОТСУТСТВУЮЩИХ КЛАССОВ (из ошибки R8)
-keep class javax.lang.model.element.Modifier { *; }
-dontwarn javax.lang.model.**

# ЗАЩИТА ОСНОВНЫХ КОМПОНЕНТОВ
-keep class com.analyzer.smsbeta.** { *; }
-keepclassmembers class com.analyzer.smsbeta.** { *; }

# ЗАЩИТА ANDROID КОМПОНЕНТОВ
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ОБРАБОТКА УСТАРЕВШИХ API
-dontwarn android.telephony.**
-dontwarn okhttp3.**
-dontwarn okio.**

# УДАЛЕНИЕ ЛОГОВ
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
    public static *** e(...);
}
