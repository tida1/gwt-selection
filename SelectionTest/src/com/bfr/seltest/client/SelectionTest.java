package com.bfr.seltest.client;

import com.bfr.client.selection.*;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SelectionTest implements EntryPoint, ClickHandler
{
    private RichTextEditor m_rte;
    
    private Button m_getCurr;
    private Button m_setHtml;
    private Button m_toSCursor;
    private Button m_toECursor;
    private Button m_surround;
    private Button m_deleteSel;
    private Button m_reset;
    
    private TextBox m_startNode;
    private TextBox m_startOffset;
    private TextBox m_endNode;
    private TextBox m_endOffset;
    private Button m_select;
    private Button m_cursor;
    
    private TextArea m_html;
    private TextArea m_sel;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
	DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
	
	m_rte = new RichTextEditor()
	{
	    @Override
	    public void onSelectionChange(Range selection)
	    {
		refresh(selection);
	    }
	};
	
	FlowPanel buts = new FlowPanel();
	//m_getCurr = new Button("Refresh v", this);
	m_setHtml = new Button("Set html ^", this);
	m_setHtml.setTitle("Set html from the lower left text area");
	m_toSCursor = new Button("< To Cursor", this);
	m_toSCursor.setTitle("Set the selection to be a cursor at the beginning of the current selection");
	m_toECursor = new Button("To Cursor >", this);
	m_toECursor.setTitle("Set the selection to be a cursor at the end of the current selection");
	m_surround = new Button("Surround", this);
	
	Grid grid = new Grid(2, 2);
	m_startNode = createTextBox(1);
	m_startOffset = createTextBox(3);
	m_endNode = createTextBox(4);
	m_endOffset = createTextBox(5);
	m_select = new Button("`>Select", this);
	m_select.setTitle("Select the texts/offsets in the boxes above");
	m_cursor = new Button("`>Cursor", this);
	m_cursor.setTitle("Set cursor to text/offset of top 2 boxes above");
	grid.setWidget(0, 0, m_startNode);
	grid.setWidget(0, 1, m_startOffset);
	grid.setWidget(1, 0, m_endNode);
	grid.setWidget(1, 1, m_endOffset);
	
	m_deleteSel = new Button("Delete", this);
	m_reset = new Button("Reset", this);
	
	//buts.add(m_getCurr);
	buts.add(m_setHtml);
	buts.add(m_toSCursor);
	buts.add(m_toECursor);
	buts.add(m_surround);
	buts.add(grid);
	buts.add(m_select);
	buts.add(m_cursor);
	
	buts.add(m_deleteSel);
	buts.add(m_reset);
	
	dlp.addWest(buts, 100);
	
	DockLayoutPanel textPanels = new DockLayoutPanel(Unit.PCT);
	
	m_html = new TextArea();
	m_html.setSize("100%", "100%");
	m_sel = new TextArea();
	m_sel.setSize("100%", "100%");
	
	textPanels.addEast(m_sel, 50);
	textPanels.addWest(m_html, 50);
	
	dlp.addSouth(textPanels, 300);
	
	dlp.add(m_rte);
	
	
	RootLayoutPanel rp = RootLayoutPanel.get();
	rp.add(dlp);
	rp.setWidgetTopBottom(dlp, 0, Unit.PX, 0, Unit.PX);
	rp.setWidgetLeftRight(dlp, 0, Unit.PX, 0, Unit.PX);
	
	DeferredCommand.addCommand(new Command()
	{
	    @Override
	    public void execute()
	    {
		m_html.setFocus(true);
	    }
	});
	
	reset();
    }
    
    private TextBox createTextBox(int startVal)
    {
	TextBox res = new TextBox();
	res.setWidth("35px");
	res.setValue("" + startVal);
	return res;
    }
    
    private void reset()
    {
	m_rte.setHtml(
	    "The <span style=\"font-weight: bold;\">quick</span> " +
	    "<span style=\"font-style: italic;\">brown </span>" +
	    "fox jumped<br>ov" +
	    "<a href=\"http://google.com\">er </a>" +
	    "<span style=\"text-decoration: underline;\">" +
	    "<a href=\"http://google.com\">th</a>e la</span>zy dogs<br>" +
	    "Some&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; spaces<br>");
    }
    
    private void refresh()
    {
	refresh(m_rte.getRange());
    }
    
    private void refresh(Range rng)
    {
	m_html.setValue(m_rte.getHtml());
	if (rng != null)
	{
	    if (rng.isCursor())
	    {
		RangeEndPoint rep = rng.getCursor();
		m_sel.setValue(rep.toString());
	    }
	    else
	    {
		m_sel.setValue(rng.getHtmlText());
	    }
	}
	else
	{
	    m_sel.setValue("");
	}
    }
    
    private void delete()
    {
	Range rng = m_rte.getRange();
	if ((rng != null) && !rng.isCursor())
	{
	    rng.deleteContents();
	    refresh();
	}
    }
    
    private void toHtml()
    {
	m_rte.setHtml(m_html.getValue());
    }
    
    private void toCursor(boolean start)
    {
	Range rng = m_rte.getRange();
	if ((rng != null) && !rng.isCursor())
	{
	    rng.collapse(start);
	    m_rte.getSelection().setRange(rng);
	    refresh();
	}
    }
    
    private void surround()
    {
	Range rng = m_rte.getRange();
	if ((rng != null) && !rng.isCursor())
	{
	    rng.surroundContents();
	    m_rte.getSelection().setRange(rng);
	    refresh();
	}
    }
    
    private Text findNodeByNumber(int num)
    {
	Text res;
	
	Document doc = m_rte.getDocument();
	for (res = Range.getAdjacentTextElement(doc, true);
	     (res != null) && (num > 0);
	     res = Range.getAdjacentTextElement(res, true))
	{
	    num--;
	}
	
	return res;
    }
    
    private void selectNodes(boolean fullSel)
    {
	int startNode = Integer.parseInt(m_startNode.getText());
	int startOffset = Integer.parseInt(m_startOffset.getText());
	
	Text startText = findNodeByNumber(startNode);
	Text endText;
	int endOffset;
	if (fullSel)
	{
	    int endNode = Integer.parseInt(m_endNode.getText());
	    endOffset = Integer.parseInt(m_endOffset.getText());
	    endText = findNodeByNumber(endNode);
	}
	else
	{
	    endText = startText;
	    endOffset = startOffset;
	}
	
	Range rng = new Range(new RangeEndPoint(startText, startOffset),
	                      new RangeEndPoint(endText, endOffset));
	
	m_rte.getSelection().setRange(rng);
	
	refresh();
    }
    
    @Override
    public void onClick(ClickEvent event)
    {
	Widget wid = (Widget)event.getSource();
	
	if (wid == m_getCurr)
	{
	    refresh();
	}
	else if (wid == m_deleteSel)
	{
	    delete();
	}
	else if (wid == m_reset)
	{
	    reset();
	}
	else if (wid == m_toECursor)
	{
	    toCursor(false);
	}
	else if (wid == m_toSCursor)
	{
	    toCursor(true);
	}
	else if (wid == m_surround)
	{
	    surround();
	}
	else if (wid == m_setHtml)
	{
	    toHtml();
	}
	else if (wid == m_select)
	{
	    selectNodes(true);
	}
	else if (wid == m_cursor)
	{
	    selectNodes(false);
	}
    }
    
    public static void setFocus(final FocusWidget wid)
    {
	DeferredCommand.addCommand(new Command()
	{
	    public void execute()
	    {
		wid.setFocus(true);
	    }
	});
    }
}
