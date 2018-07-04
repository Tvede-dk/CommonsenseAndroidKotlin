# Introduction
This library is a layer upon the android platform, trying to lift out bad design design decisions, and other annoyances. including the tremendous boilerplate code that is required in android programming.



# Why use this library ?



# Installation

The project is accessible though jcenter, so there should be a jcenter() in the repositories.

It is recommended to store the version 1 place only, such as;
```gralde
buildscript {
  ext.commonsenseVersion = "0.0.14"
}
````
Then you can start importing each of the sub modules. a full list is:

```gradle
    implementation "com.commonsense.android.kotlin:base:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:system:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:widgets:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:tools:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:prebuilt:$commonsenseVersion"
```
If you want to use all modules, then there is an "all" which contains all the submodules; in gradle:
```gradle
    implementation "com.commonsense.android.kotlin:all:$commonsenseVersion"
```

The artifacts can be viewed at: https://bintray.com/tvede-oss/Commonsense-android-kotlin

# More documentation
see [(https://github.com/Tvede-dk/CommonsenseAndroidKotlin/documentation/intro.md)]