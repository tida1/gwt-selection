## Introduction ##
gwt-selection is a small GWT library implementing cross-browser selection/cursor manipulation.  For most developers, this is useful for implementing a rich Rich Text Editor, where you may want to get the cursor or current selection.  It can also be used to manipulate textual ranges/selections on any page content.  It does not currently deal with non-textual selections such as images.

## Usage ##
Download the main library jar from the Downloads page, add it to your build path, and include it in your gwt.xml file:
```
<inherits name='com.bfr.client.Selection'/>
```

### Concept ###
This library is intended for DOM element level manipulation.  If you "just want to know the cursor position in the text like with textarea", well, think about it a minute.  The RTE contains html, not plain text, and while we certainly could compute such a number, what could you actually do with it?  Using it to alter the text form would obliterate any formatting, while trying to divine where it is in the html string and perform string manipulations is fraught with problems.

Instead, the library returns DOM text nodes and offsets to indicate cursor/selection position.  While it seems more complicated, in most cases you're better off manipulating the DOM directly to accomplish your task.  This is especially true given that there are several operations on Range that manipulate the DOM for you, such as surroundContents to surround an arbitrary selection with a span element.

### Range ###
The main object type is a _Range_, which is comprised of two _RangeEndPoint_ s, one for the start and one for the end.  If there is just a cursor and no selection, then start and end are the same.  A _RangeEndPoint_ is a pointer to a DOM text node, and an offset number of characters into that node.  For example, given this HTML:

```
<div>Wherefore <b>ar|t</b> <span>thou</span></div>
```

with a cursor indicated by |, the text node is "art" and the offset is 2.

Ranges and RangeEndPoints exist independently from the current actual selection/cursor of a page.  You may freely create, modify, etc a Range without affecting the selection.  The _Selection_ object has the method getRange() to get the range of the current selection, and setRange() to set the selection.  Again a cursor is just a Range with the same start/end point.

Finally, a _Selection_ object simply represents the selection for a given DOM document.  This is a singleton per document, so you only need to get it once even as the actual selection changes.  Once the Selection is obtained, you can use its methods get and set the selection on its document.  A window has a single document representing all content within it, except for content in frames; each frame has its own separate document.  A GWT RichTextEditor is implemented inside an iframe, so to manipulate its selection you need to get the Selection object for the iframe's document.

## Bounding Box ##
Also included in the library is the HtmlBBox, used for representing and computing the location on the page of ranges and elements.  This is useful, for instance, if you need to actually place an element on the page relative to the selection.  Note that to compute the bounding box of a range, the library modifies the dom document, by creating spans of the selected text to calculate the position from.  The original nodes are restored once the calculation is complete.

## Demo ##
The SelectionTest download is a small unpolished test/demo project, which includes a Rich Text Editor implementation.  The notable use of selection is a smart Link button, which figures out if you have an existing link selected, and in general just works like you'd expect.

The rest of the page is just test features; the bottom left text panel shows the bare html from the RTE, and the right panel shows what is currently selected (or the current cursor location) in the RTE.  Buttons:

  * Set Html ^: sets the RTE to contain the html in the bottom left panel
  * To Cursor: Sets the cursor to the start/end of the current selection
  * Surround: Surrounds the current selection with a span
  * `>Select: Selects based on the boxes above; the left is which text element and the right is how many characters offset into that element
  * `>Cursor: Same but just sets cursor based on the first row
  * Delete: Deletes selected text...