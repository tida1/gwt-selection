gwt-selection is a cross-browser implementation of the W3C selection/range standard, and some DOM editing tools to go with it.  This will let you get the cursor location or the current selection in a Rich Text Editor and on any HTML page in general.

This project was originally inspired by the Selection module in [Rocket Gwt](http://code.google.com/p/rocket-gwt/wiki/Selections) by Miroslav P.


### Usage ###
Download the main library jar from the Downloads page, add it to your build path, and include it in your gwt.xml file:

```
<inherits name='com.bfr.client.Selection'/>
```

See the [Wiki](SimpleGuide.md) for more details..

### Future ###
I've placed this project here for now in the hopes that others find it useful, and that eventually it (or the functionality) will be incorporated into GWT itself, where I think it really belongs.  See:

  * [Issue 1127](http://code.google.com/p/google-web-toolkit/issues/detail?id=1127), add your star if you agree
  * [Original GWT groups posting](http://groups.google.com/group/google-web-toolkit/browse_thread/thread/44f7b52732af5047/022af186c9626484?lnk=gst&q=selection#022af186c9626484)

I'll keep it updated with new features/fixes I encounter with my project's heavy usage.  But note that I don't come from a js background, and created this with a lot of trial and error.  So while everything seems to work pretty well, it's quite likely I did something inane...  Anyone else interested in making improvements is certainly welcome!