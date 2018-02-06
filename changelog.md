#Changelog


##0.1.0
in this release the focus have been to
- correct grammar issues (breaking changes)
- add a lot more documentation
- move extensions around into more suitable locations ( breaking changes)
- further refine the logging part, to now be fully controllable as well as a very simple but powerful plugin mechanism
- a lot of smaller things (eg extensions)
- fragments and the nullable changes in support library 27
    - The changes in the support library have made things more safe, but far more complex, with an incredible amount of null checking / guarding required.
    - 
- the demo app is now composed of sections containing examples of different features. 

###Forward
the focus forward is trying to both stabilize the api,
but at the same time be willing to create changes that fit better into the android and kotlin ecosystems.
  
(eg when the support library changes nullability, this library follows and tries to make it easier to adapt)

##0.0.11
  
###bugs
- fixed last issues with JobContainer

##0.0.10

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


