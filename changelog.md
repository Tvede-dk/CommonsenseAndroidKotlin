# Changelog

## 0.0.28
- fixed issue where setContentView was called before onCreate
- bumped 3party deps

## 0.0.27
- renamed async "startActivityWithResult" that was not named async (causes overload resolution ambiguity)

## 0.0.26
- fix android 11 package visibility & query for media/ images

## 0.0.25
- updated versions
- bumped target sdk to 30

## 0.0.24
- versions updated
- base recycler adapter does not require a context for construction, allowing to be used in say fragments much cleaner.

## 0.0.23
- spelling mistake in BaseFragmentPagerAdapter (from "setPrimiaryItem" to "setPrimaryItem") 
- fixed weird crash from BaseFragmentPagerAdapter

## 0.0.22
- updated versions
- fixed bug with base recycler (argument names was switched in one of the insertAll functions) and more exception handling
- prepared for future versions of android.x (annotations).


## 0.0.21
- newer versions 
- all updates from 0.0.19 to 0.0.19.6

## 0.0.20
- fully android x (all migrated)
- with fix from 0.0.18

## 0.0.19.6
- updated kotlin to 1.3.41 & coroutines to 1.2.2
- gradle 5.5.1
- fixed issue where startActivityWithData could end up reusing the same index / id.
- more logging via prettyString for various baseDataBinding Activities (with data)

## 0.0.19.5
- fixed weird issue where trying to fix weird bug in android keyboards, caused google keyboard sometimes to skip a char and delete the one previous of it. 

## 0.0.19.4
- fixed issue with using null / nullable types in activity with data (fragments with data as well.)
- kotlin 1.3.31
- coroutines 1.2.1

## 0.0.19.3
- fixed bug with iterating over sparsarray in foreachIndexed. (used key rather than index to get value.)

## 0.0.19.2
- reupload as there are issues with the 0.0.19.1

## 0.0.19.1
- coroutines 1.2.0 - improved scheduling performance on android (less work on main thread)
- gradle 5.4
- android gradle 3.4

## 0.0.19

- permissions handling is now possible with multiple values, and a way improved callback mechanism as well as many more extensions making life a lot easier
- fixed bug with PictureRetriever where the permission for the Camera was not acquired.
- kotlin 1.3.30
- gradle 5.4-rc1
- android gradle 3.3.2
- minor version updates for testing.
- removed thumbnail from PictureRetriever as it was not fully implemented. 

## 0.0.18
- fixed minor bug with dialogFragments and baseFragmentPagerAdapter (a bug from the support libs)

## 0.0.17
- fragment with data (still needs testing and API refinement, so its still experimental)
- kotlin 1.3.20
- coroutines 1.1.1
- added more extensions missing 
    - context functions
    - invokeEachWith (for 1 arg unit return)

- add more missing tests
- more extensions
- fixed issues with job scheduler container / and a crash when cleaning up with remaining local jobs 
- fixed issues with BaseDataBindingFragment used as dialog and then containing child fragments; 
- gradle 5.2-rc-1
- more work on BaseFragmentPagerAdapter
- added gradle script to bump all "max errors" to ridiculous levels to avoid missing the real error (like DataBinding swallows other compilation errors)
- more functions to deviceSettings
- strict mode configurable in baseApplication
- coroutine start for base splash activity changed


## 0.0.16
- preview of fragment with data
- kotlin updated to 1.3
- coroutines version 1.1.0

## 0.0.15

### Breaking changes

- Selection handling have had an overhaul, as there were missing things, such as removing views, and comments.
    - There were changes to interfaces ToggleableView, and CheckableStatusCallback , now requiring a clearOnChangedListener function.
- (BaseActivity) use (permission) and askAndUsePermission
    are now changed to simply "usePermission"
- (BaseActivity) addOnbackPressedListener and the remove are now propperly renamed to
    addOnBackPressedListener / removeOnBackPressedListener

- mapLazy now gives the argument that were optional in the "nonNull" case.
- mapLazy for null / not null and "map" for null/ not null is now named
    -mapNull
    -mapNullLazy
    so the "boolean" map is never confused.

- removed DataComposer in prebuilt.
    
### Changes

- added long missing counterparts to "visibleOrGone" , akk the "invisible" is also there now.
- a whack ton of documentation
- backported more things, and minor moving around.
- more typealiases for functions.
- added flags parameter to multiple functions regarding intents 
- map on boolean for suspend functions

- tools backport (with newer naming schema).
    - please note this might still be changed, and only using the feature enabling functions are recommended
    - added lifecycle tracking
    - added activity tracker

- backported from base module
    - algorithms package (running average)
    - useOr
    - useRefOr
    - measureOptAsyncCallback
    - measureOptAsync
    - TimeUnit improved drastically. (still waiting for kotlin 1.3 to allow inline classes).

- Preloading views to drastically improve loading times.
    - this comes in various forms; the baseAsyncLayoutInflater, which is used in
    - BaseSplashActivity.

- system services extensions;
    - now you can write "context.services.vibrator" and get the vibrator service.
    - there is another way currently, while waiting for inline classes, to allow a 0 overhead call; that is on a context you can access the services as a variable.
        - this will properly be removed in the future to not clutter up the Context (which is quite bloated as is already).

- Demo app updated
    - uses the preloading (splash screen)
    - "working" condition; it still needs a lot of work.
    - added widgets section.
    

- Pretty printing
    - core things such as base activity and base fragment and ect have a "prettyprint" /which is also the toString method.
    it allows to print the internal state of the object in a pretty way.
    So now introspection (via debugger or via logger) is now super simple for these more complex objects.
    - This will be added to more objects as time goes on



- fixed an issue with the SwipeRefreshRecyclerView accessing binding at constructor time.
    - it also now de-attaches the listener when the view gets detached.


- Selection handling
    - The "asToggleable" extension now works on all compound buttons.
    
- BaseDataBinding recycler adapter
    - drastically improved the lookup performance.
     in case of a lot sections this went from multiple ms to below 1000'th of a ms
     in the minor (with few sections) it went till around the same if not still lower than the original algorithm
     (since it gets called quite a lot this improve properly more than lab tests shows)
    - added reload section
    - added reload all
    
     
- Logging is now available from / for all classes via "logClassX" (where X is either debug,warning, error or production)
    - this should make it much simpler and to avoid writing stuff like "logWarning(this::class,"message")"
    

- added binary search as an algorithm
    -  hereto added a "comparison" enum which acts as the old "less than, greater than , equal to".
    - added binary search for regular lists, and for sparse arrays as well as int arrays.
    
- added a very generic but simple "type()" function that will give a desired class type back
    - so for example in the case of BaseRenders where the constructor takes a class type, you had to write
       ````kotlin
          MySuperLongViewBinding::class
        ````
        but now that is
        ````kotlin
          type()
        ````

- added functions regading "startActivityAndFinish"

- kotlin version 1.2.71
- coroutines version 0.30.0
- andorid support lib 28
- android gradle 3.2.0
- gradle 4.10.2

## 0.0.14
micro update

### Updates
- kotlin to 1.2.51
- gradle-bintray-plugin to 1.8.3
- gradle to 4.9-rc1
- coroutines to 0.23.4 
- mockito core to 2.19.0

### Other
cleanup in build.gradle scripts
 

## 0.0.13
micro update

### Updates
- kotlin to 1.2.50
- coroutines to 0.23.3
- gradle to 4.8.1
- gradle-bintray-plugin to 1.8.1

## 0.0.12
in this release:
- kotlin versions updates
- logging updates
- more error logging to help developers.



### Updates
- kotlin to 1.2.20
- kotlin coroutines to 0.21.2

### Additions

- added error message for using focusable for ExtendedEditTextView
### Features added / updated
vastly improved the logging functionality

- added contains multiple controls for disabling log types
- added production log "channel" that is special since its only meant for application logs, that should be allowed in production.
- added documentation for theses improvements
- broke the isLoggingAllowed as it previous was a field, now its a function. 
- added test for these additions / improvements.
- added LoggingType


## 0.0.11
  
### Bugs
- fixed last issues with JobContainer

## 0.0.10

### Bugs
- issues with concurrent modification in JobContainer
- BaseActivityData's data is now using a getter; this is to an attempt to avoid collisions with androids activity cleaning and kotlin's lazy keyword (data turning null)
### Additions
- further logging when errors encountered in BaseActivityData
### Removed
- None
### Deprecated
- None
### Features added
- None


