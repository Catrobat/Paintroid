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
# resetToOrigin the original source file name.
#-renamesourcefileattribute SourceFile


# Keep all Kotlin serialization annotations and classes
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep @kotlinx.serialization.Serializable class *

# Keep all classes that might be serialized/deserialized
-keepclassmembers class * {
    public <init>(...");
    public void set*(***);
    public *** get*();
}

# Keep specific classes related to float[] serialization in Paintroid
-keep class org.catrobat.paintroid.** { *; }

# Keep custom serializers (if any)
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep class * implements java.io.Serializable { *; }

