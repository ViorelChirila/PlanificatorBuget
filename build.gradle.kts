// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.1" apply false
//    id ("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
//    id("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.hiltPlugin) apply false
}