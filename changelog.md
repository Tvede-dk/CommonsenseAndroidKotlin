#Changelog

##0.0.13 
micro update

### Updates
- kotlin to 1.2.50
- coroutines to 0.23.4
- gradle to 4.8.1
- gradle-bintray-plugin to 1.8.1

##0.0.12
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


