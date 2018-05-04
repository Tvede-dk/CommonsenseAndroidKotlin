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
- Crypto
    - The crypto in Systems is a more sensible wrapper to handle the difficulties with crypto
    - created Aes + Hmac combo so for android before api 19, there is a encryption + integrity option
    - 

- fixes a rather large bug from earlier, according to documentation (very wildly scattered)
    issue with "activity with data"; so long story short is that when android empties processes, the static space in java is actually purged, leaving nothing behing, 
    this however causes havoc when relying on it; so in short, one is to save data (still not via a bundle, but instead store it ) in onStop, and then later retrive it.
    
    
- Developer tools
    - ANR 
    - Fps tool
    - Crash listener
    - Performance tool
- cleartext traffic setting helper (Extension, see eg https://koz.io/android-m-and-the-war-on-cleartext-traffic/)   
- api level functions now works with lint, unless otherwise specified.


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


