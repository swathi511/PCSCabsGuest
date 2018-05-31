# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/hjsoft/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn okio.**
-dontwarn android.support.**
-dontwarn retrofit2.**
-dontwarn sun.misc.Unsafe

-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.facebook.infer.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.mixpanel.**

# gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *

-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit.http.** <methods>;
}

#-keepclassmembers,allowobfuscation interface * {
 #   @retrofit.http.** <methods>;
#}

-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-keepclassmembers class * {
  public void *(android.view.View);
}

-keepclassmembers class com.payu.custombrowser.** {
      *;
      }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
     }

-renamesourcefileattribute SourceFile
#-keepattributes  Signature,SourceFile,LineNumberTable
#-keepattributes *Annotation*
-keepattributes JavascriptInterface
#-keepattributes Exceptions
-keep public class * extends android.app.Application
-keep public class com.payu.sdk.ProcessPaymentActivity$PayUJavaScriptInterface
-keep public class * implements com.payu.sdk.ProcessPaymentActivity$PayUJavaScriptInterface

#-keepattributes InnerClasses,Deprecated,EnclosingMethod

-keepattributes Exceptions, InnerClasses,*Annotation*, Signature, Deprecated, SourceFile, LineNumberTable,EnclosingMethod

-dontwarn com.pubnub.**
-keep class com.pubnub.** { *; }

-dontwarn org.slf4j.**



