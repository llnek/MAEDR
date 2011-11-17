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
 
package com.zotoh.maedr.device.apache;

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;

/**
 * When using Apache HTTP, this acts as the async callback mechanism
 * to resume and return responses back to clients.
 *
 * @author kenl
 */
abstract class HttpCB  {
    
    private Logger ilog() {  return _log=getLogger(HttpCB.class);    }
    private transient Logger _log= ilog();    
    private final HttpIO _dev;
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    
    /**
     * @return
     */
    public HttpIO getDevice() {        return _dev;    }
    
    
    /**
     * @param res
     * @param rsp
     */
    protected void replyService(HttpEventResult res, HttpResponse rsp)  {        
        
        Map<String,String> hdrs= res.getHeaders();
        StreamData data  = res.getData();
        try  {            
            for (String n : hdrs.keySet())            {
                // apache http doesnt like you setting this explicitly
                if ( "content-length".equalsIgnoreCase(n) 
                        || "content-transfer-encoding".equalsIgnoreCase(n))
                {}
                else
                { rsp.setHeader(n, hdrs.get(n)) ; }
            }            
            if (data==null || !data.hasContent()) {
                // HTTP Core doesnt like it
//                rsp.setHeader("content-length", "0") ;                
            }
            rsp.setStatusCode(res.getStatusCode()) ;
            rsp.setReasonPhrase(res.hasError() ? res.getErrorMsg() : res.getStatusText()) ;
            if (data != null && data.hasContent()) {
                rsp.setEntity(new InputStreamEntity(data.getStream(), data.getSize())) ;
            }  
        }
        catch (Exception e) {        
            tlog().warn("",e);
        }
        
    }
    
    /**
     * @param dev
     */
    protected HttpCB(HttpIO dev) {
        tstObjArg("source-device", dev) ;
        _dev=dev;
    }
    
    /**
     * @param ssl
     * @param req
     * @param ctx
     * @return
     * @throws IOException
     */
    protected Event createEvent( boolean ssl, HttpRequest req, HttpContext ctx) 
    throws IOException {
        HttpEvent ev= (HttpEvent) HttpHplr.extract(_dev, req, ctx);        
        ev.setSSL(ssl) ;
        return ev;
    }
    
    
}
