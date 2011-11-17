/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
 

package com.zotoh.maedr.device.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.http.UriUte;

/**
 * Helper functions to get data from Http requests coming from netty.
 * 
 * @author kenl
 */
public enum NettpHplr {
; /* no instance */

	/**
	 * @param key
	 * @return
	 */
	public static String calcHybiSecKeyAccept(String key) {
	    // add fix GUID according to 
	    // http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-10
	    String k = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	    String rc ="";	
	    try {
    		MessageDigest md = MessageDigest.getInstance("SHA-1");
	        byte[] bits = md.digest(k.getBytes("utf-8"));
	        rc = Base64.encodeBase64String(bits);
	    } 
	    catch (Exception e) {
	    		//TODO
	    }
	    return rc;
	}
    
    /**
     * @param dev
     * @param req
     * @return
     */
    public static HttpEvent extract( NettpIO dev, HttpRequest req) {
        
        HttpEvent ev= dev.createEvent();
        int pos;
        String uri= req.getUri();

		// for REST, we want to inspect the URL more closely by breaking up into paths.
		// this is really to deal with possible matrix type parameters
		if ( !ev.setUriChain( UriUte.toPathChain(uri) )) {
			getParams(ev, uri);			
		}
        
        ev.setProtocol ( req.getProtocolVersion().getProtocolName());
        ev.setMethod( req.getMethod().getName());
                
        pos = uri.indexOf("?") ;
        if (pos >= 0) {
            ev.setQueryString( uri.substring(pos+1) ) ;
            uri= uri.substring(0,pos) ;
        }
        
        ev.setUri( uri );
        
        for (Map.Entry<String, String> h: req.getHeaders()) {
            ev.setHeader(h.getKey(), h.getValue()) ;            
        }
                
        return ev;
    }
    
    /**
     * @param cbuf
     * @param os
     * @throws IOException
     */
    public static void getContent(ChannelBuffer cbuf, OutputStream os) 
    				throws IOException {
        
        int len, clen= cbuf.readableBytes();
        //int pos= cbuf.readerIndex();
        byte[] bits= new byte[4096] ;
        
        while (clen > 0) {
            len = Math.min(4096, clen) ;
            cbuf.readBytes(bits, 0, len) ;
            os.write(bits, 0, len) ;
            clen = clen-len;
        }
        os.flush();
    }
    
    private static void getParams(HttpEvent ev, String uri) {
        
        QueryStringDecoder dec = new QueryStringDecoder( uri);       
        Map<String, List<String>> 
        params = dec.getParameters();
        
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p: params.entrySet()) {
                ev.addParam(p.getKey(), p.getValue().toArray(new String[0]) );
            }
        }
        
    }
    
}
