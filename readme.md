# Introduction
This library is a layer upon the android platform, trying to lift out bad design design decisions, and other annoyances. including the tremendous boilerplate code that is required in android programming.

# Why then use this library ?

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

### Launching activities

*Vanilla*
```kotlin
    startActivity(Intent(this, MainActivity::class.java))
}

```

*With Csense*
```kotlin
    //
    startActivity(MainActivity::class)
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

###


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
see Intro https://github.com/Tvede-dk/CommonsenseAndroidKotlin/blob/master/documentation/Intro.md