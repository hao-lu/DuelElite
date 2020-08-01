object Versions {
    const val MIN_SDK = 21
    const val COMPILE_SDK = 28
    const val TARGET_SDK = 28

    const val KOTLIN = "1.3.72"
}

object GradlePlugin {
    const val ANDROID = "com.android.tools.build:gradle:4.0.0"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
    const val NAVIGATION_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:2.2.1"
    const val REALM = "io.realm:realm-gradle-plugin:5.15.1"
}

object Dependencies {
    const val JSOUP = "org.jsoup:jsoup:1.10.3"
    const val KOTLIN_STDLIB_JDK7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.KOTLIN}"
    const val KOTLIN_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8"
    const val MOSHI = "com.squareup.moshi:moshi:1.9.3"
    const val MOSHI_KOTLIN = "com.squareup.moshi:moshi-kotlin:1.9.3"
    const val PICASSO = "com.squareup.picasso:picasso:2.5.2"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:2.3.0"
}

object AndroidX {
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.1.0"
    const val CORE = "androidx.core:core:1.1.0"
    const val CORE_KTX = "androidx.core:core-ktx:1.1.0"
    const val CONSTRAINTLAYOUT = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val LEGACY_SUPPORT_V4 = "androidx.legacy:legacy-support-v4:1.0.0"
    const val LIFECYCLE_EXTENSION = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val MATERIAL = "com.google.android.material:material:1.1.0-rc02"
    const val NAVIGATION_FRAGMENT = "androidx.navigation:navigation-fragment:2.2.1"
    const val NAVIGATION_UI = "androidx.navigation:navigation-ui:2.2.1"
    const val PALETTE = "androidx.palette:palette:1.0.0"
    const val ROOM_COMPILER = "androidx.room:room-compiler:2.2.5"
    const val ROOM_KTX = "androidx.room:room-ktx:2.2.5"
    const val ROOM_RUNTIME = "androidx.room:room-runtime:2.2.5"
    const val VIEWPAGER2 = "androidx.viewpager2:viewpager2:1.0.0"
}

object Tests {
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.1.1"
    const val EXT_JUNIT = "androidx.test.ext:junit:1.1.0"
    const val JUNIT = "junit:junit:4.12"
}