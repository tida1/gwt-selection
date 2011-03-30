package com.bfr.seltest.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.*;

public interface EditSymbolImageBundle extends ClientBundle
{   
    public static final EditSymbolImageBundle INST = 
	(EditSymbolImageBundle)GWT.create(EditSymbolImageBundle.class);;
    
    public ImageResource bold_icon();
    public ImageResource horizontalrule_icon();
    public ImageResource indentless_icon();
    public ImageResource indentmore_icon();
    public ImageResource italics_icon();
    public ImageResource justifycenter_icon();
    public ImageResource justifyleft_icon();
    public ImageResource justifyright_icon();
    public ImageResource link_icon();
    public ImageResource list_icon();
    public ImageResource noformat_icon();
    public ImageResource numberedlist_icon();
    public ImageResource strikethrough_icon();
    public ImageResource subscript_icon();
    public ImageResource superscript_icon();
    public ImageResource underline_icon();
}
