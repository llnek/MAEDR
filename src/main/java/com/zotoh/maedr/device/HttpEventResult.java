/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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


import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.util.Map;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.NCMap;
import com.zotoh.netio.HTTPStatus;

/**
 * @author kenl
 *
 */
public class HttpEventResult extends EventResult {
    
    private static final long serialVersionUID= 568732487697863245L;
    private final NCMap<String> _headers=new NCMap<String>();
    private StreamData _data;
    private String _text= "OK";
    private int _code= 200;
//    private long _cLen;
    
    /**
     * @param s
     */
    public HttpEventResult(HTTPStatus s)    {
        setStatus(s);
    }
    
    /**
     * 
     */
    public HttpEventResult()
    {}
    
    /**
     * Get the result payload data.
     * 
     * @return result payload.
     */
    public StreamData getData()    {         return _data;    }
    
    /**
     * Set the payload data.
     * 
     * @param d data.
     */
    public void setData(StreamData d)    {
        if (d != null)
        try {
           // _cLen= d.getSize();
        }
        catch (Exception e) {
            tlog().warn("", e);
        }
        
        _data=d;        
    }
    
    /**
     * @param data
     * @throws IOException
     */
    public void setData(String data)    throws IOException {
        _data= data==null ? null : new StreamData(asBytes(data));
    }
    
    /**
     * Set the error message.
     * 
     * @param msg message.
     */
    public void setErrorMsg(String msg)    {
        _text= nsb(msg);
        setError(true);
    }
    
    /**
     * Get the error message to be sent back.
     * 
     * @return error message.
     */
    public String getErrorMsg()     {
        String rc="";
        if ( hasError()) {
            rc= isEmpty(_text) ? getStatusText() : _text; 
        }
        return rc; 
    }
    
    /**
     * @param s
     */
    public void setStatus(HTTPStatus s) {
        setStatusText( s.getReasonPhrase() );
        setStatusCode(s.getCode());
    }
    
    /**
     * Set the HTTP status code to be sent back.
     * 
     * @param c status code.
     */
    public void setStatusCode(int c)    {
        //setError( ! (c >= 200 && c < 300) );
        _code= c;
    }

    /**
     * @param s
     */
    public void setStatusText(String s)    {        _text= nsb(s);    }
    
    /**
     * @return
     */
    public String getStatusText()    {        return _text;    }
    
    /**
     * Get the HTTP status code to be sent back.
     * 
     * @return the code.
     */
    public int getStatusCode()    {          return _code;    }
        
    /**
     * Get all the internet headers.
     * 
     * @return immutable map.
     */
    public Map<String,String> getHeaders()    { 
        return unmodifiableMap(_headers); 
    }
    
    /**
     * Add another internet header, overwrite existing one if same.
     * 
     * @param h header key.
     * @param v value.
     */
    public void setHeader(String h, String v)    {
        if (!isEmpty(h))
        { _headers.put(h, nsb(v)); }
    }
    
    /**
     * 
     */
    public void clearAllHeaders()    {
        _headers.clear();
    }
        
}
