/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
 *
 * THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 * MODIFY IT UNDER THE TERMS OF THE APACHE LICENSE, 
 * VERSION 2.0 (THE "LICENSE").
 *
 * THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 * BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *   
 * SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING PERMISSIONS 
 * AND LIMITATIONS UNDER THE LICENSE.
 *
 * You should have received a copy of the Apache License
 * along with this distribution; if not, you may obtain a copy of the 
 * License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 ??*/
 
package com.zotoh.maedr.device;

import static com.zotoh.core.util.StrUte.*;

/**
 * Result for Websockets.
 * 
 * @author kenl
 */
public class WebSockResult extends EventResult {
    
    private static final long serialVersionUID = 7636284597211747987L;
    private boolean _isText;
    private String _textData;
    private byte[] _binData;
    
    /**
     * 
     */
    public WebSockResult() {
    }
    
    /**
     * @param text
     */
    public void setData(String text) {
        _textData=nsb(text);
        _isText=true;
    }
    
    /**
     * @param bits
     */
    public void setData(byte[] bits) {
        _binData=bits;
        _isText=false;
    }

    /**
     * @return
     */
    public boolean isText() { return _isText; }

    /**
     * @return
     */
    public boolean isBinary() { return !isText(); }
    
    /**
     * @return
     */
    public String getText() { return _textData; }
    
    /**
     * @return
     */
    public byte[] getBinary() { return _binData; }
    
}
