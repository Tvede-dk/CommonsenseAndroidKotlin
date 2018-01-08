# Status

[![Build Status](https://travis-ci.org/Tvede-dk/CommonsenseAndroidKotlin.svg?branch=master)](https://travis-ci.org/Tvede-dk/CommonsenseAndroidKotlin)

//SonarQube here as well

//and more stats.

#Installation

Currently the project is not accessible to maven central, but will be later.
For now, add the following to global build.gralde

```gralde
buildscript {
  ext.commonsenseVersion = "0.0.11"
  repositories {
        maven { url "https://dl.bintray.com/tvede-oss/Commonsense-android-kotlin" } // <----------
  }
}

````
Then you can start importing each of the sub modules. a full list is:

```gradle

    implementation "com.commonsense.android.kotlin:base:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:prebuilt:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:system:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:tools:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:widgets:$commonsenseVersion"

```
The current version is: 0.0.11

The artifacts can be viewed at: https://bintray.com/tvede-oss/Commonsense-android-kotlin

# Introduction 

This library is a layer upon the android platform, trying to lift out bad design design decisions, and other annoyances. including the tremendous boilerplate code that is required in android programming.

##  Sub components
The project is layered into a number of components, each laying a foundation for others to build apron.

There is an inter-dependency between the components. However taking base for example does not mean you have to take system.

### Base



### System

### Test

### Views (widgets)

### Prebuilt 
