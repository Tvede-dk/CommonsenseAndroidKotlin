# Changelog

## 0.0.15

### Breaking changes

- Selection handling have had an overhaul, as there were missing things, such as removing views, and comments.
    - There were changes to interfaces ToggleableView, and CheckableStatusCallback , now requiring a clearOnChangedListener function.
- (BaseActivity) use (permission) and askAndUsePermission
    are now changed to simply "usePermission"
- (BaseActivity) addOnbackPressedListener and the remove are now propperly renamed to
    addOnBackPressedListener / removeOnBackPressedListener



### Changes

- backported more things, and minor moving around.
- more typealiases for functions.
- map on boolean for suspend functions
- gradle 4.9
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
    - BaseActivity
    - BaseFragment

- system services extensions;
    - now you can write "context.services.vibrator" and get the vibrator service.
    - there is another way currently, while waiting for inline classes, to allow a 0 overhead call; that is on a context you can access the services as a variable.
        - this will properly be removed in the future to not clutter up the Context (which is quite bloated as is already).

- Demo app updated
    - uses the preloading
    - "working" condition; it still needs a lot of work.


- fixed an issue with the SwipeRefreshRecyclerView accessing binding at constructor time.
    - it also now de-attaches the listener when the view gets detached.


- Selection handling
    - The "asToggleable" extension now works on all compound buttons.

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


