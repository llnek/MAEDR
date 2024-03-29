/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import com.zotoh.core.io.StreamData;
import static com.zotoh.core.util.StrUte.*;

/**
 * Events generated by a POP3 device.
 *
 * @author kenl
 */
public class POP3Event extends Event {

    private static final long serialVersionUID = -5093250293811551815L;
    private final StreamData _msg;
    private final String _hdrs;
    
    /**
     * @param dev
     * @param headers
     * @param msg
     */
    public POP3Event(Device dev, String headers, StreamData msg)     {
        super(dev);
        _msg= msg;
        _hdrs = nsb(headers);
    }

    /**
     * @return
     */
    public StreamData getMsg() {        return _msg;    }
    
    /**
     * @return
     */
    public String getHeaders() {        return _hdrs;    }
    
    /**
     * @return
     */
    public String getSubject() {        return getLine("subject:");    }
    
    /**
     * @return
     */
    public String getTo() {        return getLine("to:");           }
    
    /**
     * @return
     */
    public String getFrom() {        return getLine("from:");                    }
    
    /**
     * @param key
     * @return
     */
    private String getLine(String key) {
        String[] s= _hdrs.split("\\r\\n");
        String rc="";
        if (s != null) for (int i=0; i < s.length; ++i) {
            if ( s[i].toLowerCase().indexOf(key) >= 0) {
                rc= s[i];
                break;
            }
        }
        return rc;
    }
    
}
