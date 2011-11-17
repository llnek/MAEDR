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


import static com.zotoh.core.io.StreamUte.createTempFile;
import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Tuple;

/**
 * Helper functions to get data from a Http request.
 * 
 * @author kenl
 */
public enum HttpHplr {
; /* no instance */

	/**
	 * @param dev
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public static HttpEvent extract(BaseHttpIO dev, HttpServletRequest req) 
					throws IOException   {
	    
		HttpEvent ev= new ServletEvent(dev);
		int clen= req.getContentLength();
		String s;
		String[] vals;
		long thold= dev.getThreshold();
		
		ev.setContentType(req.getContentType());
		ev.setContentLength( clen);
		
		req.getContextPath();
		
		ev.setMethod( req.getMethod() );
		ev.setServletPath( req.getServletPath());
		
		ev.setSSL( nsb(req.getScheme()).toLowerCase().indexOf("https") >= 0 );
		ev.setScheme(req.getScheme()) ;
		
		ev.setUrl( nsb( req.getRequestURL()) );
		ev.setUri( req.getRequestURI() );
		
		ev.setQueryString( req.getQueryString() );
		ev.setProtocol(req.getProtocol());
		
		for (Enumeration<?> en= req.getHeaderNames(); en.hasMoreElements();) {
			s=nsb( en.nextElement());
			ev.setHeader( s, req.getHeader(s) );			
		}
		
		for (Enumeration<?> en= req.getParameterNames(); en.hasMoreElements();) {
			s= nsb( en.nextElement());
			vals=req.getParameterValues(s) ;
			ev.addParam(s, vals) ;
		}
		
		for (Enumeration<?> en= req.getAttributeNames(); en.hasMoreElements();) {
			s=nsb( en.nextElement());
			ev.addAttr(s, req.getAttribute(s)) ;			
		}

		if (clen > 0) {
            grabPayload(ev, req.getInputStream(), clen, thold);
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
    
}
