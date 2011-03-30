package com.bfr.seltest.client;

import com.bfr.client.selection.*;
import com.bfr.seltest.client.images.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class RichTextEditor extends Composite implements HasChangeHandlers
{   
    //private static final String BUTTON_WIDTH = "25px";
    
    private DockLayoutPanel m_mainPanel;
    private HorizontalPanel m_toolbarPanel;
    
    private RichTextArea m_textW;
    private Range m_lastRange;
    private boolean m_isInText = false;
    
    private Formatter m_formatter;
    
    // All the buttons for formatting
    private ToggleButton m_boldW;
    private ToggleButton m_italicW;
    private ToggleButton m_underlineW;
    private ToggleButton m_subscriptW;
    private ToggleButton m_superscriptW;
    private ToggleButton m_strikethroughW;
    private PushButton m_indentW;
    private PushButton m_outdentW;
    private PushButton m_justifyLeftW;
    private PushButton m_justifyCenterW;
    private PushButton m_justifyRightW;
    private PushButton m_hrW;
    private PushButton m_olW;
    private PushButton m_ulW;
    private PushButton m_newLinkW;
    private PushButton m_removeFormatW;
    
    private String m_lastText = "";
    
    public boolean trigger = false;
    
    // For catching events on these
    private EventListener m_listener = new EventListener();
    
    // Timer for trying real time selection change stuff
    private Range m_timerRange = null;
    private Timer m_selTimer = new Timer()
    {
	@Override
	public void run()
	{
	    try 
	    {
		Range rng = getSelection().getRange();
		if ((m_timerRange == null) || (!m_timerRange.equals(rng)))
		{
		    onSelectionChange(rng);
		    m_timerRange = rng;
		}
	    } 
	    catch (Exception ex)
	    {
		GWT.log("Error in timer selection", ex);
	    }
	}
    };
    
    public RichTextEditor()
    {
	m_mainPanel = new DockLayoutPanel(Unit.EM);
	m_toolbarPanel = new HorizontalPanel();
	//m_toolbarPanel.setWidth("100%");
	m_toolbarPanel.addStyleName("timeline-RichTextToolbar");
	
	m_textW = new RichTextArea();
	m_textW.addClickHandler(m_listener);
	m_textW.addKeyUpHandler(m_listener);
	m_textW.addMouseDownHandler(m_listener);
	m_textW.addBlurHandler(m_listener);
	m_textW.addMouseOutHandler(m_listener);
	m_textW.addMouseOverHandler(m_listener);
	// According to gwt doc, these do need to be set because this is a frame
	m_textW.setHeight("100%");
	m_textW.setWidth("100%");
	
	// Add buttons
	m_formatter = getFormatter();
	
	EditSymbolImageBundle icons = EditSymbolImageBundle.INST;
	
	m_boldW = addToggleButton(m_toolbarPanel, 
	                          icons.bold_icon(), 
	                          "Bold");
	m_italicW = addToggleButton(m_toolbarPanel, 
	                            icons.italics_icon(), 
	                            "Italic");
	m_underlineW = addToggleButton(m_toolbarPanel, 
	                               icons.underline_icon(), 
	                               "Underline");
	m_subscriptW = addToggleButton(m_toolbarPanel, 
	                               icons.subscript_icon(), 
	                               "Subscript");
	m_superscriptW = addToggleButton(m_toolbarPanel, 
	                                 icons.superscript_icon(), 
	                                 "Superscript");
	m_strikethroughW = addToggleButton(m_toolbarPanel, 
	                                   icons.strikethrough_icon(), 
	                                   "Strikethrough");
	
	m_indentW = addPushButton(m_toolbarPanel, 
	                          icons.indentmore_icon(), "Indent Right");
	m_outdentW = addPushButton(m_toolbarPanel, 
	                           icons.indentless_icon(), "Indent Left");
	m_justifyLeftW = addPushButton(m_toolbarPanel, 
	                               icons.justifyleft_icon(), 
	                               "Justify Left");
	m_justifyCenterW = addPushButton(m_toolbarPanel, 
	                                 icons.justifycenter_icon(), 
	                                 "Justify Center");
	m_justifyRightW = addPushButton(m_toolbarPanel, 
	                                icons.justifyright_icon(), 
	                                "Justify Right");
	m_hrW = addPushButton(m_toolbarPanel, 
	                      icons.horizontalrule_icon(), "Horizontal Rule");
	m_olW = addPushButton(m_toolbarPanel, 
	                      icons.numberedlist_icon(), "Numbered List");
	m_ulW = addPushButton(m_toolbarPanel, icons.list_icon(), "List");
	m_newLinkW = addPushButton(m_toolbarPanel, 
	                           icons.link_icon(), "Link Document");
	m_removeFormatW = addPushButton(m_toolbarPanel, 
	                                icons.noformat_icon(), "No Format");
	
	m_mainPanel.addNorth(m_toolbarPanel, 2.0);
	m_mainPanel.add(m_textW);
	    
	initWidget(m_mainPanel);
	//initWidget(m_scrollW);
	this.sinkEvents(Event.ONCLICK);
    }
    
    public Formatter getFormatter()
    {
	return m_textW.getFormatter();
    }
    
    public RichTextArea getRichTextArea() {return m_textW;}
    
    /*
    private PushButton addPushButton(HorizontalPanel panel,
	    			     String text,
	    			     String tip) 
    {
	PushButton pb = new PushButton(text);
	addAnyButton(panel, pb, tip);
	return pb;
    }
    */

    private PushButton addPushButton(HorizontalPanel panel,
	    			     ImageResource imagep,
	    			     String tip) 
    {
	PushButton pb = new PushButton(new Image(imagep));
	addAnyButton(panel, pb, tip);
	return pb;
    }

    /*
    private ToggleButton addToggleButton(HorizontalPanel panel,
	    				 String text,
	    				 String tip) 
    {
	ToggleButton tb = new ToggleButton(text);
	addAnyButton(panel, tb, tip);
	return tb;
    }
    */
    
    private ToggleButton addToggleButton(HorizontalPanel panel,
	    				 ImageResource imagep,
	    				 String tip) 
    {
	ToggleButton tb = new ToggleButton(new Image(imagep));
	addAnyButton(panel, tb, tip);
	return tb;
    }
    
    private void addAnyButton(HorizontalPanel panel, 
                              ButtonBase button, 
                              String tip)
    {
	button.addStyleName("richText-button");
	button.setTitle(tip);
	//button.setWidth(BUTTON_WIDTH);
	button.setHeight("100%");
	panel.add(button);
	//panel.setCellWidth(button, BUTTON_WIDTH);
	button.addClickHandler(m_listener);
    }
    
    private class EventListener 
    	    implements ClickHandler, KeyUpHandler, BlurHandler, 
    	    	       MouseOutHandler, MouseOverHandler, MouseDownHandler
    {
	public void onClick(ClickEvent event)
	{
	    Widget sender = (Widget)event.getSource();
	    if (sender == m_boldW) 
	    {
		m_formatter.toggleBold();
	    } else if (sender == m_italicW) 
	    {
		m_formatter.toggleItalic();
	    } else if (sender == m_underlineW) 
	    {
		m_formatter.toggleUnderline();
	    } else if (sender == m_subscriptW) 
	    {
		m_formatter.toggleSubscript();
	    } else if (sender == m_superscriptW) 
	    {
		m_formatter.toggleSuperscript();
	    } else if (sender == m_strikethroughW) 
	    {
		m_formatter.toggleStrikethrough();
	    } else if (sender == m_indentW) 
	    {
		m_formatter.rightIndent();
	    } else if (sender == m_outdentW) 
	    {
		m_formatter.leftIndent();
	    } else if (sender == m_justifyLeftW) 
	    {
		m_formatter.setJustification(RichTextArea.Justification.LEFT);
	    } else if (sender == m_justifyCenterW) 
	    {
		m_formatter.setJustification(RichTextArea.Justification.CENTER);
	    } else if (sender == m_justifyRightW) 
	    {
		m_formatter.setJustification(RichTextArea.Justification.RIGHT);
	    } else if (sender == m_hrW) 
	    {
		m_formatter.insertHorizontalRule();
	    } else if (sender == m_olW) 
	    {
		m_formatter.insertOrderedList();
	    } else if (sender == m_ulW) 
	    {
		m_formatter.insertUnorderedList();
	    } else if (sender == m_removeFormatW) 
	    {
		m_formatter.removeFormat();
	    } else if (sender == m_newLinkW)
	    {
		EventLinkPopup.open(RichTextEditor.this);
	    } else if (sender == m_textW) 
	    {
		updateStatus();
	    }
	    checkForChange();
	}
	
	public void onKeyUp(KeyUpEvent event)
	{
	    Widget sender = (Widget)event.getSource();
	    if (sender == m_textW)
	    {
		updateStatus();
		checkForChange();
	    }
	}
	
	public void onMouseDown(MouseDownEvent event)
	{
	    trigger = true;
	}
	
	public void onBlur(BlurEvent event)
	{
	    checkForChange();
	}

	@Override
	public void onMouseOut(MouseOutEvent event)
	{
	    if (m_isInText && isOnTextBorder(event))
	    {
		m_isInText = false;
		captureSelection();
		endSelTimer();
	    }
	}
	
	@Override
	public void onMouseOver(MouseOverEvent event)
	{
	    if (!m_isInText)
	    {
		m_isInText = true;
		m_textW.setFocus(true);
		m_lastRange = null;
		startSelTimer();
	    }
	}
    }
    
    /**
    * This captures the selection when the mouse leaves the RTE, because in IE
    * the selection indicating the cursor position is lost once another widget
    * gains focus.  Could be implemented for IE only.
    */
    public void captureSelection()
    {
	try 
	{
	    m_lastRange = getSelection().getRange();
	} 
	catch (Exception ex)
	{
	    GWT.log("Error capturing selection for IE", ex);
	}
    }
    
    // Gets run every time the selection is changed
    public void onSelectionChange(Range sel)
    {
    }
    
    @SuppressWarnings("unchecked")
    private boolean isOnTextBorder(MouseEvent event)
    {
	Widget sender = (Widget)event.getSource();
	int twX = m_textW.getAbsoluteLeft();
	int twY = m_textW.getAbsoluteTop();
	int x = event.getClientX() - twX;
	int y = event.getClientY() - twY;
	int width = m_textW.getOffsetWidth();
	int height = m_textW.getOffsetHeight();
	return ((sender == m_textW) &&
	        ((x <= 0) || (x >= width) || 
	         (y <= 0) || (y >= height)));
    }
    
    public void startSelTimer()
    {
	m_selTimer.scheduleRepeating(250);
    }
    
    public void endSelTimer()
    {
	m_selTimer.cancel();
    }
    
    public Range getRange()
    {
	if (m_lastRange == null)
	{
	    return getSelection().getRange();
	}
	else
	{
	    return m_lastRange;
	}
    }
    
    public Selection getSelection()
    {
	Selection res = null;
	try
	{
	    JavaScriptObject window = getWindow();
	    res = Selection.getSelection(window);
	}
	catch (Exception ex)
	{
	    GWT.log("Error getting the selection", ex);
	}
	return res;
    }
    
    public JavaScriptObject getWindow()
    {
	IFrameElement frame = m_textW.getElement().cast();
	return getWindow(frame);
    }
    
    public static native JavaScriptObject getWindow(IFrameElement iFrame)
    /*-{
	var iFrameWin = iFrame.contentWindow || iFrame.contentDocument;

	if( !iFrameWin.document ) 
	{
	    iFrameWin = iFrameWin.getParentNode(); //FBJS version of parentNode
	}
	return iFrameWin;
    }-*/;
    
    public Document getDocument()
    {
	return Selection.getDocument(getWindow());
    }
    
    public void setHtml(String text)
    {
	m_textW.setHTML(text);
	m_lastText = text;
    }
    
    public String getHtml()
    {
	return m_textW.getHTML();
    }
    
    private void checkForChange()
    {
	String text = m_textW.getHTML();
	if (!text.equals(m_lastText))
	{
	    NativeEvent nEvt = Document.get().createChangeEvent();
	    ChangeEvent.fireNativeEvent(nEvt, RichTextEditor.this);
	    m_lastText = text;
	}
    }
    
    // Update edit buttons based on current cursor location
    private void updateStatus() 
    {
	if (m_formatter != null) 
	{
	    m_boldW.setDown(m_formatter.isBold());
	    m_italicW.setDown(m_formatter.isItalic());
	    m_underlineW.setDown(m_formatter.isUnderlined());
	    m_subscriptW.setDown(m_formatter.isSubscript());
	    m_superscriptW.setDown(m_formatter.isSuperscript());
	    m_strikethroughW.setDown(m_formatter.isStrikethrough());
	}
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler)
    {
	return addDomHandler(handler, ChangeEvent.getType());
    }

}
