/*
 * Copyright 2010 John Kozura
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bfr.client.selection;

import com.google.gwt.dom.client.*;
import com.google.gwt.regexp.shared.RegExp;

/**
* An end point of a range, represented as a text node and offset in to it.
* Does not support potential other types of selection end points.
* 
* @author John Kozura
*/
public class RangeEndPoint implements Comparable<RangeEndPoint>
{
    public static final short MOVE_CHARACTER 	= 1;
    public static final short MOVE_WORDSTART	= 2;
    public static final short MOVE_WORDEND	= 3;
    
    // All unicode whitespace characters
    public static final String DEFAULT_WS_REXP =
	"[\t-\r \u0085\u00A0\u1680\u180E\u2000-\u200B\u2028\u2029\u202F\u205F\u3000\uFEFF]+";
    
    @SuppressWarnings("unused")
    private static RegExp c_wsRexp;
    
    /**
    * Set the regular expression used for detecting consecutive whitespace in a 
    * string.  It must be of the form "[ \t\n]+", with all desired whitespace 
    * characters between the braces.  This is used for word detection for
    * the move method.
    * 
    * @param regExp String of the regular expression
    */
    public static void setWhitespaceRexp(String regExp)
    {
	c_wsRexp = RegExp.compile(regExp, "gm");
    }
    
    /**
    * The node containing the start/end of the selection.  This can be a 
    * Text for normal text selections, or an Element for nontextual, for
    * instance an image. 
    */
    private Node m_node;
    
    /**
    * The number of characters starting from the beginning of the textNode
    * where the selection begins/ends.
    */
    private int m_offset;
    
    /**
    * Create a range end point with nothing set
    */
    public RangeEndPoint()
    {
	super();
    }
    
    /**
    * Create a range end point at the start or end of an element.  The actual
    * selection will occur at the first/last text node within this element.
    * 
    * @param element element to create this end point in
    * @param start whether to make the end point at the start or the end
    */
    public RangeEndPoint(Element element, boolean start)
    {
	this();
	setElement(element, start);
    }
    
    /**
    * Create a non-textual range end point that just points to this element
    * 
    * @param element Non-textual element to point to
    */
    public RangeEndPoint(Element element)
    {
	this();
	setElement(element);
    }
    
    /**
    * Create a range end point at the start or end of a text node
    * 
    * @param text text node this end point starts/end in
    * @param start whether to make the end point at the start or the end
    */
    public RangeEndPoint(Text text, boolean start)
    {
	this();
	setTextNode(text, start);
    }
    
    /**
    * Create a range end point with a text node and offset into it
    * 
    * @param text text node this end point occurs in
    * @param offset offset characters into the text node
    */
    public RangeEndPoint(Text text, int offset)
    {
	this();
	
	setTextNode(text);
	setOffset(offset);
    }
    
    /**
    * Clone a range end point
    * 
    * @param endPoint point to clone
    */
    public RangeEndPoint(RangeEndPoint endPoint)
    {
	this(endPoint.getTextNode(), endPoint.getOffset());
    }
    
    @Override
    public int compareTo(RangeEndPoint cmp)
    {
	Range thisRng = new Range(this);
	Range cmpRng = new Range(cmp);
	return thisRng.compareBoundaryPoint(cmpRng, Range.START_TO_START);
    }
    
    @Override
    public boolean equals(Object obj)
    {
	boolean res = false;
	
	try
	{
	    RangeEndPoint cmp = (RangeEndPoint)obj;
	    
	    res = (cmp == this) ||
	      	  ((cmp.getTextNode() == getTextNode()) &&
	    	   (cmp.getOffset() == getOffset()));
	}
	catch (Exception ex) {}
	
	return res;
    }
    
    /**
    * Get the offset into the text node
    * 
    * @return offset in characters
    */
    public int getOffset()
    {
	return m_offset;
    }
    
    /**
    * Get the string of the text node of this end point, either up to or 
    * starting from the offset:
    * 
    * "som|e text"
    *   true  : "e text"
    *   false : "som"
    * 
    * @param asStart whether to get the text as if this is a start point
    * @return the text before or after the offset, or null if this is not set
    */
    public String getString(boolean asStart)
    {
	if (!isTextNode())
	{
	    return null;
	}
	String res = ((Text)m_node).getData();
	return asStart ? res.substring(m_offset) : res.substring(0, m_offset);
    }
    
    /**
    * Get the text node of this end point, note this can be null if there are
    * no text anchors available or if this is just an element.
    * 
    * @return the text node
    */
    public Text getTextNode()
    {
	return isTextNode() ? (Text)m_node : null;
    }
    
    /**
    * Get the Element of this end point, if this is not a textual end point.
    * 
    * @return the text node
    */
    public Element getElement()
    {
	return isTextNode() ? null : (Element)m_node;
    }
    
    @SuppressWarnings("deprecation")
    public boolean isSpace(Character check)
    {
	return Character.isSpace(check);
    }
    
    public boolean isTextNode()
    {
	return (m_node == null) ? false 
				: (m_node.getNodeType() == Node.TEXT_NODE);
    }
    
    /**
    * If the offset occurs at the beginning/end of the text node, potentially
    * move to the end/beginning of the next/previous text node, to remove
    * text nodes where 0 characters are actually used.  If asStart is true then
    * move a cursor at the end of a text node to the beginning of the next;
    * vice versa for false.
    * 
    * @param asStart Whether to do this as a start or end range point
    */
    public void minimizeBoundaryTextNodes(boolean asStart)
    {
	Text text = getTextNode();
	if ((text != null) && 
	    (m_offset == (asStart ? text.getLength() : 0)))
	{
	    Text next = Range.getAdjacentTextElement(text, asStart);
	    if (next != null)
	    {
		setTextNode(next);
		m_offset = asStart ? 0 : next.getLength();
	    }
	}
    }
    
    /**
    * TODO IMPLEMENTED ONLY FOR CHARACTER
    * Move the end point forwards or backwards by one unit of type, such as
    * by a word.  
    * 
    * @param forward true if moving forward
    * @param topMostNode top node to not move past, or null
    * @param limit an endpoint not to move past, or null
    * @param type what unit to move by, ie MOVE_CHARACTER or MOVE_WORD
    * @param count how many of these to move by
    * @return how far this actually moved
    */
    public int move(boolean forward, 
                    Node topMostNode, 
                    RangeEndPoint limit,
                    short type, 
                    int count)
    {
	int res = 0;
	
	Text limitText = (limit == null) ? null : limit.getTextNode();
	Text curr = getTextNode();
	if (curr != null)
	{
	    Text last = curr;
	    int offset = getOffset();
	    
	    switch (type)
	    {
	    case MOVE_CHARACTER:
		while (curr != null)
		{
		    last = curr;
		    int len = forward ? curr.getLength() - offset : offset;
		    if (curr == limitText)
		    {
			// If there is a limiting endpoint, may not be able to
			// go as far
			len = forward ? (limit.getOffset() - offset) 
				: (offset - limit.getOffset());
		    }
		    
		    if ((len + res) < count)
		    {
			res += len;
			
			if (curr == limitText)
			{
			    break;
			}
		    }
		    else
		    {
			// Finis
			len = count - res;
			offset = forward ? (offset + len) : offset - len;
			res = count;
			break;
		    }
		    
		    do
		    {
			// Next node, skipping any 0-length texts
			curr = Range.getAdjacentTextElement(curr, topMostNode, 
			                                    forward, false);
		    } while  ((curr != null) && (curr.getLength() == 0));
		    
		    if (curr != null)
		    {
			offset = forward ? 0 : curr.getLength();
		    }
		    
		}
		break;
	    /*
	    case MOVE_WORDSTART:
	    case MOVE_WORDEND:
		if (c_wsRexp == null)
		{
		    setWhitespaceRexp(DEFAULT_WS_REXP);
		}
		
		while (curr != null)
		{
		    
		    do
		    {
			// Next node, skipping any 0-length texts
			curr = Range.getAdjacentTextElement(curr, topMostNode, 
			                                    forward, false);
		    } while  ((curr != null) && (curr.getLength() == 0));
		    
		    if (curr != null)
		    {
			offset = forward ? 0 : curr.getLength();
		    }
		}
		break;
		*/
	    default:
		assert(false);
	    }
	    setTextNode(last);
	    setOffset(offset);
	}
	return res;
    }
    
    /**
    * Sets this start point to be a non-textual element (like an image)
    */
    public void setElement(Element element)
    {
	m_node = element;
    }
    
    /**
    * Set the range end point at the start or end of an element.  The actual
    * selection will occur at the first/last text node within this element.
    * 
    * @param element element to set this end point in
    * @param start whether to make the end point at the start or the end
    */
    public void setElement(Element element, boolean start)
    {
	Text text = Range.getAdjacentTextElement(element, element, 
	                                         start, false);
	setTextNode(text, start);
    }
    
    /**
    * Set the offset into the text node
    * 
    * @param offset offset in characters
    */
    public void setOffset(int offset) 
    {
	m_offset = offset;
    }
    
    /**
    * Set the text node this end point occurs in
    * 
    * @param text text node this end point occurs in
    */
    public void setTextNode(Text textNode)
    {
	m_node = textNode;
    }
    
    /**
    * Set this range end point at the start or end of a text node
    * 
    * @param text text node this end point starts/end in
    * @param start whether to make the end point at the start or the end
    */
    public void setTextNode(Text textNode, boolean start)
    {
	setTextNode(textNode);
	setOffset((start || (textNode == null)) ? 0 : textNode.getLength());
    }
     
    /**
    * Get the text of this with a "|" at the offset
    * 
    * @return a string representation of this endpoint
    */
    @Override
    public String toString()
    {
	return getString(false) + "|" + getString(true);
    }
}
