package com.bfr.seltest.client;

import com.bfr.client.selection.*;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

import java.util.List;

public class EventLinkPopup extends DecoratedPopupPanel implements ClickHandler
{   
    private static final int LABEL_WIDTH = 85;
    private static final int ROW_HEIGHT = 24;
    
    private RichTextEditor m_editor;
    private Selection m_sel;
    private Range m_range;
    private List<Text> m_selTexts;
    private String m_origTargetText = "";
    private RangeEndPoint m_origAnchorStart;
    private RangeEndPoint m_origAnchorEnd;
    
    private TextBox m_webPageText;
    private TextBox m_targetText;
    private CheckBox m_fillOutCB;
    private Button m_okBut;
    private Button m_cancelBut;
    
    private EventLinkPopup(RichTextEditor editor)
    {
	super(false, true);
	m_editor = editor;
	
	setGlassEnabled(true);
	
	FlowPanel vpanel = new FlowPanel();
	vpanel.setWidth("300px");
	
	m_webPageText = new TextBox();
	m_webPageText.setValue("http://");
	m_webPageText.setWidth("100%");
	
	vpanel.add(m_webPageText);
	
	Label lbl = new Label("Display:");
	
	m_targetText = new TextBox();
	m_targetText.setWidth("100%");
	
	LayoutPanel lpanel = new LayoutPanel();
	lpanel.add(lbl);
	lpanel.setWidgetLeftWidth(lbl, 0, Unit.PX, LABEL_WIDTH, Unit.PX);
	lpanel.add(m_targetText);
	lpanel.setWidgetLeftRight(m_targetText, LABEL_WIDTH, Unit.PX, 
	                          0, Unit.PX);
	lpanel.setPixelSize(300, ROW_HEIGHT);
	
	vpanel.add(lpanel);
	
	m_fillOutCB = new CheckBox("Change entire link");
	m_fillOutCB.setVisible(false);
	m_fillOutCB.addClickHandler(this);
	vpanel.add(m_fillOutCB);
	
	m_okBut = new Button("Ok", this);
	m_okBut.addStyleName("float-left");
	
	m_cancelBut = new Button("Cancel", this);
	m_cancelBut.addStyleName("float-left");
	
	FlowPanel hpanel = new FlowPanel();
	hpanel.add(m_okBut);
	hpanel.add(m_cancelBut);
	
	vpanel.add(hpanel);
	
	setWidget(vpanel);
    }
    
    public static EventLinkPopup open(RichTextEditor editor)
    {
	EventLinkPopup popup = new EventLinkPopup(editor);
	if (popup.refresh())
	{
	    popup.center();
	}
	else
	{
	    popup = null;
	}
	return popup;
    }
    
    public boolean refresh()
    {
	try
	{
	    m_sel = m_editor.getSelection();
	    
	    m_range = m_editor.getRange();
	    if (m_range == null)
	    {
		return false;
	    }
	    else
	    {
		m_selTexts = m_range.getSelectedTextElements();
		if (m_selTexts == null)
		{
		    return false;
		}
		else
		{
		    m_origTargetText = m_range.getText();
		    m_targetText.setValue(m_origTargetText);
		    
		    AnchorElement anchor = getAnchor(m_selTexts);
		    if (anchor != null)
		    {
			String href = anchor.getHref().trim();
			if (!href.isEmpty())
			{
			    m_webPageText.setValue(href);
			}
			
			m_origAnchorStart = getAnchorLimit(
			                      m_range.getStartPoint().getTextNode(),
			                      anchor, false);
			m_origAnchorEnd = getAnchorLimit(
			                      m_range.getStartPoint().getTextNode(),
			                      anchor, true);
			
			if (m_range.getStartPoint().equals(m_origAnchorStart) &&
			    m_range.getStartPoint().equals(m_origAnchorEnd))
			{
			    m_origAnchorStart = null;
			    m_origAnchorEnd = null;
			}
			else
			{
			    m_fillOutCB.setVisible(true);
			    m_fillOutCB.setValue(true);
			    
			    m_origTargetText = fetchStringFromTexts(
			                  m_origAnchorStart, m_origAnchorEnd);
			    m_targetText.setValue(m_origTargetText);
			}
		    }
		}
	    }
	}
	catch (Exception ex)
	{
	    return false;
	}
	return true;
    }
    
    public boolean apply()
    {
	Formatter formatter = m_editor.getFormatter();
	
	String link;
	link = m_webPageText.getValue().trim();
	if (link.isEmpty())
	{
	    return false;
	}
	
	if ((m_origAnchorStart != null) && m_fillOutCB.getValue())
	{
	    // Expand selection to these bounds
	    m_range.setRange(m_origAnchorStart, m_origAnchorEnd);
	}
	// Ensure the selection hasn't changed, or at least changes to the
	// expanded bounds we want
	m_sel.setRange(m_range);
	
	String targetText = m_targetText.getValue();
	
	if (m_range.isCursor())
	{
	    // Insert into a single cursor location
	    AnchorElement newEle = AnchorElement.as(DOM.createAnchor());
	    newEle.setHref(link);
	    newEle.setInnerText(targetText);
	    
	    Text startNode = m_range.getStartPoint().getTextNode();
	    Element parentEle = startNode.getParentElement();
	    int offset = m_range.getStartPoint().getOffset();
	    String text = startNode.getData();
	    
	    if (offset == 0)
	    {
		parentEle.insertBefore(newEle, startNode);
	    }
	    else
	    {
		if (offset < text.length())
		{
		    // Split this in two and insert the new node between
		    startNode.splitText(offset);
		}
		parentEle.insertAfter(newEle, startNode);
	    }
	    m_sel.setRange(new Range(newEle));
	}
	else if (!targetText.equals(m_origTargetText))
	{
	    // Replace whatever was selected with this new text
	    Element ele = m_range.surroundContents();
	    AnchorElement newEle = AnchorElement.as(DOM.createAnchor());
	    newEle.setHref(link);
	    newEle.setInnerText(targetText);
	    ele.getParentElement().replaceChild(newEle, ele);
	    
	    m_sel.setRange(new Range(newEle));
	}
	else
	{
	    formatter.createLink(link);
	}
	
	return true;
    }
    
    public AnchorElement getAnchor(List<Text> nodes)
    {
	AnchorElement res = null;
	
	for (Text node : nodes)
	{
	    res = getAnchor(node);
	    if (res != null)
	    {
		break;
	    }
	}
	return res;
    }
    
    public AnchorElement getAnchor(Node node)
    {
	AnchorElement res = null;
	for (Element ele = node.getParentElement();
	     ele != null;
	     ele = ele.getParentElement())
	{
	    String tag = ele.getTagName();
	    if (tag.equalsIgnoreCase("A"))
	    {
		res = AnchorElement.as(ele);
		break;
	    }
	}
	return res;
    }
    
    public RangeEndPoint getAnchorLimit(Text node, 
                                        AnchorElement anchor,
                                        boolean forward)
    {
	Text prevNode;
	String href = anchor.getHref();
	do
	{
	    prevNode = node;
	    node = Range.getAdjacentTextElement(prevNode, forward);
	    if (node != null)
	    {
		AnchorElement cmpAnchor = getAnchor(node);
		if ((cmpAnchor == null) || !href.equals(cmpAnchor.getHref()))
		{
		    break;
		}
	    }
	} while (node != null);
	
	RangeEndPoint res = new RangeEndPoint();
	res.setTextNode(prevNode);
	res.setOffset(forward ? prevNode.getData().length() : 0);
	return res;
    }
    
    public long parseEventLink(String href)
    {
	long res = 0;
	int idx = href.indexOf("#event=");
	if (idx > 0)
	{
	    try
	    {
		res = Long.parseLong(href.substring(idx + 7));
	    }
	    catch (Exception ex) {}
	}
	return res;
    }
    
    public String createEventLink(long id)
    {
	return "#event=" + id;
    }
    
    public String fetchStringFromTexts(RangeEndPoint startPoint, 
                                       RangeEndPoint endPoint)
    {
	String res = null;
	List<Text> texts = Range.getSelectedTextElements(
	                      startPoint.getTextNode(), endPoint.getTextNode());
	if (texts != null)
	{
	    res = fetchStringFromTexts(texts, startPoint, endPoint);
	}
	return res;
    }
    
    public String fetchStringFromTexts(List<Text> allTexts,
                                       RangeEndPoint startPoint,
                                       RangeEndPoint endPoint)
    {
	String selText = "";
	for (Text node : allTexts)
	{
	    String val = node.getData();
	    if (node == startPoint.getTextNode())
	    {
		if (node == endPoint.getTextNode())
		{
		    val = val.substring(startPoint.getOffset(), 
		                        endPoint.getOffset());
		}
		else
		{
		    val = val.substring(startPoint.getOffset());
		}
	    }
	    else if (node == endPoint.getTextNode())
	    {
		val = val.substring(0, endPoint.getOffset());
	    }
	    selText += val;
	}
	return selText;
    }
    
    @Override
    public void onClick(ClickEvent event)
    {
	Widget sender = (Widget)event.getSource();
	if (sender == m_cancelBut)
	{
	    hide();
	}
	else if (sender == m_okBut)
	{
	    if (apply())
	    {
		hide();
	    }
	}
	else if (sender == m_fillOutCB)
	{
	    if (m_fillOutCB.getValue())
	    {
		m_origTargetText = fetchStringFromTexts(m_origAnchorStart, 
		                                        m_origAnchorEnd);
		m_targetText.setValue(m_origTargetText);
	    }
	    else
	    {
		m_origTargetText = m_range.getText();
		m_targetText.setValue(m_origTargetText);
	    }
	}
    }

    public void checkSuggestValid()
    {
	m_okBut.setEnabled(true);
    }
    
    private Command m_deferredCheckValidCmd = new Command()
    {
	@Override
	public void execute()
	{
	    checkSuggestValid();
	}
    };
    
    public void deferredCheckValid()
    {
	DeferredCommand.addCommand(m_deferredCheckValidCmd);
    }
}
