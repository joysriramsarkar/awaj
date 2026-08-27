# Proguard rules for Awaj
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
