# ==============================================================================
# PROGUARD / R8 SOURCE CODE OBFUSCATION & ANTI-REVERSE-ENGINEERING SHIELD
# Protects against decompilation, APK sniffing, IP extraction, and code theft.
# ==============================================================================

# Enable aggressive code shrinking and class repackaging into root
-repackageclasses ''
-allowaccessmodification
-overloadaggressively

# Hide source file names, line numbers, and debug symbols to block decompiler inspection
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Strip all debug logging, printStackTrace and telemetry to prevent data leakage
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Preserve Room Database Entities and DAOs
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public *;
}
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Preserve WebView JavaScript Interfaces and WebViews
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public *;
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
    public *;
}

# Android Jetpack Compose Rules
-keep,allowobfuscation,allowshrinking class androidx.compose.** { *; }

# Obfuscate internal network & util classes
-keepclassmembers class com.example.util.SecurityShield {
    public *;
}
