# Introduction

This library is divided into multiple parts, each building on top of the previous ones.

At the root is the Base module, containing very primitive things, mostly related to kotlin / java itself rather than android;
 But the massive amount of extensions will prove to be extremely useful.
Furthermore there are some data structures and patterns as well, making some annoying tasks easier (such as Expected)
Even so there are also some more "high level" things such as the compat/SSLFactory which backports TLS 1.2 to android 16 (due to android normally disabling it for api 16 up to 20)

At a more "android" specific level is the "system" which is meant at the raw android system level, so no view related stuff, or some more advanced tools / debugging concepts.
Here both the dataBinding features are implemented as activities and layouts are such a vital part, that its quite fundamental to android.
Even so there are also things that deal with logging, image handling, permissions and so on.

After the system's package, there are widgets, which contains all the views things, including a fully fledged recycler adapter, with very high performance and capable of handling a vast amount of complex setup and usage, which is enabled by both the base and system modules.
Custom View's are also a thing here, together with a growing library of widgets (highly optimized) ready to be used.

The tools library is mean to be where development tools would be presented, this could be something like inline debugging views, and performance tools (such as monitoring, ect)

The prebuilt library is meant to be application parts that are so common its used almost everywhere, but could also be seen as almost a part of an application.
It is here that the BaseApplication is, which handles setup of both developing tools as well as app compact things like vector drawable, ect.

It also contains a pre setup WebView activity to try and backport features using WebViews but; and also to create a common safe class for handling WebViews.





