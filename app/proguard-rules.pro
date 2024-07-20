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
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep class com.startapp.** {
      *;
}

# Keep serializable classes & fields
#-keep class ** extends java.io.Serializable {
#    <fields>;
#}
# Keep Android classes
#-keep class ** extends android.** {
#    <fields>;
#    <methods>;
#}
#-keep class * {
#    public private *;
#}
-allowaccessmodification
-renamesourcefileattribute SourceFile
-repackageclasses ''

-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-dontnote com.google.gson.annotations.Expose
    -keepclassmembers class * {
        @com.google.gson.annotations.Expose <fields>;
    }


    -keepclasseswithmembers,allowobfuscation,includedescriptorclasses class * {
        @com.google.gson.annotations.Expose <fields>;
    }

    -dontnote com.google.gson.annotations.SerializedName
    -keepclasseswithmembers,allowobfuscation,includedescriptorclasses class * {
        @com.google.gson.annotations.SerializedName <fields>;
    }
-keep class com.squareup.okhttp.** { *; }

-keep interface com.squareup.okhttp.** { *; }
# Retrofit
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}

-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.youtube.player.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-dontwarn com.facebook.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-ignorewarnings

