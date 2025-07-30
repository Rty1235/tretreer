# Основные настройки
-dontobfuscate
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify

# Сохраняем критичные для Android элементы
-keep public class * extends android.app.Activity
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.Context
-keepclassmembers class * extends android.content.Context {
    public void *(android.content.Intent);
}

# Шифрование строковых констант
-keep class com.analyzer.smsbeta.StringObfuscator { *; }

# Динамическая загрузка классов
-keep class com.analyzer.smsbeta.DynamicLoader { *; }

# Защита нативных методов
-keepclasseswithmembernames class * {
    native <methods>;
}

# Сохраняем обработчики системных событий
-keepclassmembers class * {
    void onReceive(android.content.Context, android.content.Intent);
}

# Обфускация пакетов
-repackageclasses ''
-allowaccessmodification

# Скрытие Telegram API данных
-keep class com.analyzer.smsbeta.TelegramApiWrapper { *; }
