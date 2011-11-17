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
 
package com.zotoh.maedr.device.apache;


import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HttpContext;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Tuple;
import com.zotoh.maedr.device.HttpEvent;

/**
 * Helper functions to get data from a Http request.
 * 
 * @author kenl
 */
public enum HttpHplr {
; /* no instance */

    public static HttpEvent extract(HttpIO dev, HttpRequest req, HttpContext ctx) 
    				throws IOException   {
        
        HttpEvent ev= new HttpEvent(dev);
        long thold= dev.getThreshold();
        Header[] hds= req.getAllHeaders();
        String head, v,ct="";
        
        if (hds != null) for (int i=0; i < hds.length; ++i) {
        	head=hds[i].getName(); v=hds[i].getValue();
        	ev.setHeader(head,v);
        	if ("content-type".equalsIgnoreCase(head)) 
        	{ ct= v; }
        }
        
        if (!isEmpty(ct)) { ev.setContentType(ct); }
    
        long clen= 0L;

        if ( req instanceof HttpEntityEnclosingRequest) {            
            HttpEntity body = ( (HttpEntityEnclosingRequest) req).getEntity();
            clen= body.getContentLength();
            grabPayload(ev, body.getContent(), clen, thold);
        }
        
        RequestLine line= req.getRequestLine();
        String str= line.getUri(), 
        uri=str, qry="";
        int pos= str.indexOf("?");
        if (pos >= 0) {
            uri= str.substring(0, pos);
            qry= str.substring(pos+1);
        }
        getParams(ev, qry) ;
        
        ev.setProtocol( req.getProtocolVersion().getProtocol() );
        ev.setContentLength(clen);
        ev.setUri( uri);
        ev.setMethod(line.getMethod());
        ev.setQueryString( qry);                        
        
        if (ev.tlog().isDebugEnabled()) {
        	ev.tlog().debug( ev.toString() ); 
    	}
        
        return ev;
    }

    private static void grabPayload(HttpEvent ev, InputStream inp, long clen, long thold)
    				throws IOException {
    	OutputStream os= null;
    	Tuple t=null;
    	
        if (clen > thold) {
            t= createTempFile(true);
            os= (OutputStream) t.get(1);
        } else {
            os= new ByteOStream(4096);
        }
        
        try  {
            streamToStream( inp, os, clen);
            ev.setData( new StreamData(t==null ? 
            					((ByteOStream)os).asBytes() : t.get(0) ) );
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        finally {
            StreamUte.close(os);
        }    	
    }
    
    private static void getParams(HttpEvent ev, String qry) throws UnsupportedEncodingException {
        String[] ss= qry.split("&") ;
        String n, v, s;
        int pos;
        
        if (ss != null) for (int i=0; i < ss.length; ++i) {
            s= ss[i];
            pos= s.indexOf("=") ;
            if (pos > 0) {
                n= trim( s.substring(0, pos) );
                v= trim( s.substring(pos+1) );
                if (! isEmpty(v)) {
                    v= URLDecoder.decode(v, "utf-8") ;
                }
                ev.addParam(n, v) ;
            }
        }
        
    }
    
}
