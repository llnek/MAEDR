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

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.EventResult;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.maedr.device.SyncWaitEvent;
import com.zotoh.maedr.device.WaitEvent;
import com.zotoh.netio.HTTPStatus;

/**
 * Synchronous CB working with Apache Http IO.  This essentially blocks the thread until
 * a result is ready.
 * 
 * @author kenl
 */
public class HttpRequestCB extends HttpCB implements HttpRequestHandler {
    
    /**
     * @param dev
     */
    public HttpRequestCB(HttpIO dev)    {       
        super(dev);
    }

    /* (non-Javadoc)
     * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
     */
    @Override
    public void handle(HttpRequest req, HttpResponse rsp, HttpContext ctx)
            throws HttpException, IOException    {        
        tlog().debug("HttpRequestCB: URI=> {}" , req.getRequestLine().getUri()) ;            
        
        final HttpIO dev = (HttpIO) getDevice();
        EventResult res;
        WaitEvent w= new SyncWaitEvent( createEvent( dev.isSSL(), req,ctx) );
        final Event ev = w.getInnerEvent();
        
        dev.holdEvent(w) ;  
        
        dev.getDeviceManager().getEngine()
        .getScheduler().run( new Runnable(){
            public void run()  {                
                dev.dispatch(ev) ;
            }            
        });
        
        try {
            w.timeoutMillis(dev.getWaitMillis());
        }
        finally {
            dev.releaseEvent(w);
        }
        
        res= w.getInnerEvent().getResult();
        
        if (res instanceof HttpEventResult)        {
            replyService( (HttpEventResult) res, rsp);
        }
        else        {
            replyService( new HttpEventResult(HTTPStatus.REQUEST_TIMEOUT), rsp);
        }
        
    }

}
