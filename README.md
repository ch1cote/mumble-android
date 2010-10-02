[Mumble](http://mumble.sourceforge.net) client for Android phones.

This is my personal fork of [pcgod's original](/pcgod/mumble-android),
official, Mumble client.

This fork has two major differences from the original:
- The contents are set up as an Eclipse project as that is my primary
IDE and more importantly the original fork did not have build scripts
commited when I forked it.
- The native binary lbraries required by the client are included in the
repository. I use both Windows and Linux for development and in Windows
the cross-compiler requires Cygwin set up. Commiting the native libraries
straight into the repository simplifies the development for me.

