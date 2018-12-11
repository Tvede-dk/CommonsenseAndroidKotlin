# Introduction
This library is a layer upon the android platform, trying to lift out bad design design decisions, and other annoyances. including the tremendous boilerplate code that is required in android programming.

# Why use this library?

The point about this library is that google is developing android as a platform, which means to allow developers to create everything, which does not necessarily translate to "easy to program for"

That is what this library tries to do, gap the difference between the platform and the common sense expectation about using it.

## Examples

### Databinding
Using androids databinding is quite boilerplate ish, and comes with quite a lot of annoying work;
For example, creating a DataBind'ed activity

*Vanilla*
```kotlin

class ExampleActivity: Activity {

    private var binding: ExampleActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?){
        binding = ExampleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //use binding, which is at this point optional
        binding?.textview.text = "someExample"
    }
}

```

*With Csense*
```kotlin

class ExampleDatabindingActivity : BaseDatabindingActivity<ExampleActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<ExampleActivityBinding> =
            ExampleActivityBinding::inflate

    override fun useBinding() {
        //use binding, which is not null anymore;
        binding.exampleActivityTextview.text = "example"
    }
}

```

### Activity tasks

Launching activities

*Vanilla*
```kotlin
    startActivity(Intent(this, MainActivity::class.java))
```

*With Csense*
```kotlin
    startActivity(MainActivity::class)
```


Finishing an Activity safely (all kinds of Activity)

*Vanilla*
```kotlin
    runOnUiThread{
        finish()
    }
```

*With Csense*
```kotlin
    safeFinish()
```



### Fragments
Pushing a new fragment to the current stack (inside of a FragmentActivity)

*Vanilla*
```kotlin
    val fragment: Fragment = ...
    val containerId : Int = R.id.some_container_id

    supportFragmentManager?.let{
        val transaction = beginTransaction()
        transaction.replace(containerId,fragment)
        transaction.addToBackStack("a uniq name")
        transaction.commit()
    }
```

*With Csense*
```kotlin
    val fragment: Fragment = ...
    val containerId : Int = R.id.some_container_id
    pushNewFragmentTo(containerId, fragment)
```

Performing transactions of the fragment manager

*Vanilla*
```kotlin
    //simple commit
    supportFragmentManager?.let {
        val transaction = beginTransaction()
        //do the work, and remeber to call comit
        // could span many lines.
        transaction.commit()
    }
    //commitNow
    supportFragmentManager?.let {
        val transaction = beginTransaction()
        //do the work, and remeber to call comit
        // could span many lines.
        transaction.commitNow()
    }
```

*With Csense*
```kotlin
    supportFragmentManager?.transactionCommit {
        //safe inlined call where commit will be called for us no matter what we do in here.
    }
    supportFragmentManager?.transactionCommitNow {
        //safe inlined call where commitNow will be called for us no matter what we do in here.
    }
```



### Asking for permissions
(see https://developer.android.com/training/permissions/requesting#make-the-request, changed to only ask for camera)
*Vanilla*
```kotlin
    val MY_PERMISSIONS_REQUEST_CAMERA = 4567
    fun askForCamera() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
                ActivityCompat.requestPermissions(thisActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            // Permission has already been granted
            //use camera
        }
    }
    // ...later in the activity

    override fun onRequestPermissionsResult(requestCode: Int,
            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //use camera
                } else {
                    //could not use camera
                }

            }
            //...
        }
    }
```


*With Csense in BaseActivity(1)*
```kotlin
    use(PermissionEnum.Camera, {
        //can use camera
    }, {
        //could not use camera
    })

```
Or more explicit

(Does not need to be a BaseActivity, as long as the PermissionHandler is setup correctly, and receives the "onRequestPermissionsResult" callback)
```kotlin
    PermissionEnum.Camera.use(permissionHandler, this, {
        //use camera
    }, {
        //could not use camera
    })
```

In the case of using kotlin coroutines / suspend

(Does not need to be a BaseActivity, as long as the PermissionHandler is setup correctly, and receives the "onRequestPermissionsResult" callback)
```kotlin
     PermissionEnum.Camera.useSuspend(permissionHandler, this, {
        //use camera, we are in a suspend function
     }, {
        //could not use camera, we are in a suspend function
     })
```

### Common view operations

Making a view visible / invisible or gone

*Vanilla*
```kotlin
    val view : View = ...
    view.visibility = View.GONE
    view.visibility = View.VISIBLE
    view.visibility = View.INVISIBLE
```

*With Csense*
```kotlin
    val view : View = ...
    view.gone()
    view.visible()
    view.invisible()
```

Querying for the visibility of a view

*Vanilla*
```kotlin
    val view : View = ...
    val isGone = view.visibility == View.GONE
    val isVisible = view.visibility == View.VISIBLE
    val isInvisible = view.visibility == View.INVISIBLE
```

*With Csense*
```kotlin
    val view : View = ...
    val isGone = view.isGone
    val isVisible = view.isVisible
    val isInvisible = view.isInvisible
```

Change visibility based on boolean

*Vanilla*
```kotlin
    val view : View = ...
    val bool = ...
    if (bool) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
```

*With Csense*
```kotlin
    val view : View = ...
    val bool = ...
    view.visibleOrGone(bool)
```

Or Just toggling it

*Vanilla*
```kotlin
    val view : View = ...
    val bool = view.visibility == View.GONE
    if (bool) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
```

*With Csense*
```kotlin
    val view : View = ...
    val bool = ...
    view.toggleVisibilityGone(bool)
```

Making a view disabled / enabled

*Vanilla*
```kotlin
    val view : View = ...
    //enable
    view.isEnabled = true
    view.isClickable = true
    //disable
    view.isEnabled = false
    view.isClickable = false
```


*With Csense*
```kotlin
    val view : View = ...
    view.enable()
    view.disable()
```
Where even ViewGroups are handled (in Csense), where as the vanilla example does not handle ViewGroups at all.


### Selection handling
whenever you want some kind of selection regarding 1 item, or you want to be able to toggle between them, then theres is the

- SingleSelectionHandler (only 1 selected at a time)
- ToggleSelectionViewHandler (every one can be selected or deselected)


### Caching views
To most peoples surprise calling "setContentView" with a layout id,
 causes the main thread to go and read from the disk,parse and deinflate an xml layout, which takes quite a while. 
However there is a possible solution, which is to use the "AsyncLayoutInflater",
 however it does have some very weird
  drawbacks;

*Vanilla*

*With Csense* 
(inside of a BaseSplashActivity)



### Logging
Commonsense ships with a very flexible, simple logging component called "L".

The extensions for views, activities ect are all using this logging, and for good reason.
It supports removing all logging functions (functions that does something with the log statements)
It supports customizing logging functions (say you want to send the logs to a server)
It supports disabling all logging no matter what 
It supports a special "production" logging channel, so some logging can be done even if production apps turn off logging
It does not require a tag in most cases , since the classname is already there (reduces boilerplate)

The internal library uses it. 

The are a lot of extensions for using it, both from views, activities and so on. 
These extensions are called "logDebug", "logWarning" , "logError", "logProduction" and does not take a tag, because that is the calling objects true code name.

So for an activity this would look like

*Vanilla*
````kotlin
class MyActivity : Activity {
    fun test() {
        Log.d(TAG,"my message")
    }

    companion object {
        val TAG = "MyActivity"
    }
}
````
*With Csense* 
````kotlin
class MyActivity : Activity {
    fun test() {
        logDebug("my message")
    }
}
````


Turning off logging 

*Vanilla*
- Not supported out of the box (hacks via proguard exists but have sideeffects)

*With Csense*
````kotlin
L.isLoggingAllowed(false)
````


Piping Logging (to eg a server)

*Vanilla* 
- not supported out of the box 

*With Csense*
````kotlin
//add a new listener for the debug "channel"
L.debugLoggers.add{ tag: String, message:String, throwable : Throwable? -> 
    ///handling logging here.
}
````


# Installation

The project is accessible though jcenter, so there should be a jcenter() in the repositories.

It is recommended to store the version 1 place only, such as;
```gralde
buildscript {
  ext.commonsenseVersion = "0.0.16"
}
````
Then you can start importing each of the sub modules. a full list is:

```gradle
    implementation "com.commonsense.android.kotlin:base:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:system:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:widgets:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:tools:$commonsenseVersion"
    implementation "com.commonsense.android.kotlin:prebuilt:$commonsenseVersion"
    //for tests
    testImplementation "com.commonsense.android.kotlin:test:$commonsenseVersion"
```
If you want to use all modules, then there is an "all" which contains all the submodules; in gradle:
```gradle
    implementation "com.commonsense.android.kotlin:all:$commonsenseVersion"
```
You will still have to add the test manually. 

The artifacts can be viewed at: https://bintray.com/tvede-oss/Commonsense-android-kotlin

# More documentation
see Intro https://github.com/Tvede-dk/CommonsenseAndroidKotlin/blob/master/documentation/Intro.md