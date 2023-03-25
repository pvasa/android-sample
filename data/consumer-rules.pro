### Sample
-keepclassmembers class dev.priyankvasa.sample.**.model.** { *; }

### kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class dev.priyankvasa.sample.**$$serializer { *; }
-keepclassmembers class dev.priyankvasa.sample.** {
    *** Companion;
}
-keepclasseswithmembers class dev.priyankvasa.sample.** {
    kotlinx.serialization.KSerializer serializer(...);
}
