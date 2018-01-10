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


> Types

There are a couple of type decleartions to make simple types look a lot nicer, since having the codebased filled with parans makes it harder to read. 
The most common onces:
````kotlin
    typealias EmptyFunction = () -> Unit
    typealias EmptyFunctionResult<T> = () -> T
    typealias AsyncEmptyFunction = suspend () -> Unit
    typealias AsyncFunctionUnit<T> = suspend (T) -> Unit
    
    /**
     * Function with 1 parameter that returns unit
     */
    typealias FunctionUnit<E> = (E) -> Unit
    typealias FunctionResult<I, O> = (I) -> O
````    

> Compat
Since android api 16 to 20 have support for TLS 1.1 and TLS 1.2 , but it is disabled, 
 you have to manually enable that for ALL sockets (by explicit setting the enabledProtocols to "TLSv1.2").
 This is tedious and a lot of annoying boilerplate, so we provide the class
````kotlin
     SSLSocketFactoryCompat
````
to handle that. (only tls 1.2, due to security.)
 
 
 
> Extensions


> Patterns


> JobContainer
This feature is the basis of job scheduling and containing (in the system package is named **UiAwareJobContainer** )

It is meant for handling 3 types of scheduling

    * Quing  (executing aware) 
    * Sequential ( as they come in , with no specific order)
    * Grouped ( if multiple of these are put in, only 1 will be ranned)
     


### System

### Test

### Views (widgets)

### Prebuilt 
