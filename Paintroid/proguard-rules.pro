# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================================================
# General Android Configuration
# ============================================================================

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============================================================================
# AndroidX Libraries
# ============================================================================

# Keep AndroidX annotations
-keep class androidx.annotation.** { *; }
-dontwarn androidx.annotation.**

# AndroidX AppCompat
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

# Material Design Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# AndroidX Core
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**

# ============================================================================
# Paintroid Model Classes (Critical for Serialization)
# ============================================================================

# Keep all model classes - these are serialized/deserialized
-keep class org.catrobat.paintroid.model.** { *; }
-keepclassmembers class org.catrobat.paintroid.model.** { *; }

# Keep all command classes - used for undo/redo functionality
-keep class org.catrobat.paintroid.command.** { *; }
-keepclassmembers class org.catrobat.paintroid.command.** { *; }

# Keep tool classes
-keep class org.catrobat.paintroid.tools.** { *; }
-keepclassmembers class org.catrobat.paintroid.tools.** { *; }

# Keep contract interfaces
-keep interface org.catrobat.paintroid.contract.** { *; }

# ============================================================================
# Kryo Serialization Library
# ============================================================================

# Keep Kryo classes
-keep class com.esotericsoftware.kryo.** { *; }
-keepclassmembers class com.esotericsoftware.kryo.** { *; }
-dontwarn com.esotericsoftware.kryo.**

# Keep serializers
-keep class com.esotericsoftware.kryo.serializers.** { *; }
-keepclassmembers class com.esotericsoftware.kryo.serializers.** { *; }

# Keep classes that Kryo might serialize
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================================
# Universal Image Loader
# ============================================================================

-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers class com.nostra13.universalimageloader.** { *; }
-dontwarn com.nostra13.universalimageloader.**

# ============================================================================
# Image Compression Library
# ============================================================================

-keep class id.zelory.compressor.** { *; }
-dontwarn id.zelory.compressor.**

# ============================================================================
# Kotlin Coroutines
# ============================================================================

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-dontwarn kotlinx.coroutines.**

# ============================================================================
# Test Libraries (Espresso Idling Resources)
# ============================================================================

# Keep Espresso idling resources if used in production code
-keep class androidx.test.espresso.idling.** { *; }
-dontwarn androidx.test.espresso.**

# ============================================================================
# Reflection and Native Methods
# ============================================================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep classes with constructors used via reflection
-keepclassmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ============================================================================
# Enums
# ============================================================================

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================================
# Parcelable Classes
# ============================================================================

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================================================
# Remove Logging (Optional - Uncomment for Release)
# ============================================================================

# Remove all logging in release builds
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
#     public static *** i(...);
#     public static *** w(...);
# }

# ============================================================================
# Optimization Settings
# ============================================================================

# Enable optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Don't warn about missing classes in optional dependencies
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.Metadata
