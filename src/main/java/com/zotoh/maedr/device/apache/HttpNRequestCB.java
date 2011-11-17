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

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpResponseTrigger;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;

import com.zotoh.maedr.device.AsyncWaitEvent;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.WaitEvent;

/**
 * Callback class working with async apache Http NIO.
 *
 * @author kenl
 */
public class HttpNRequestCB extends HttpCB implements  NHttpRequestHandler  {
    
    /**
     * @param dev
     */
    public HttpNRequestCB(HttpIO dev)    {
        super(dev) ;
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.protocol.NHttpRequestHandler#entityRequest(org.apache.http.HttpEntityEnclosingRequest, org.apache.http.protocol.HttpContext)
     */
    @Override
    public ConsumingNHttpEntity entityRequest(
            HttpEntityEnclosingRequest req,  HttpContext ctx) throws HttpException, IOException    {
//        return new BufferingNHttpEntity(req.getEntity(), new HeapByteBufferAllocator());
        return new StreamingNHttpEntity(req.getEntity(), new HeapByteBufferAllocator());
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.protocol.NHttpRequestHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.nio.protocol.NHttpResponseTrigger, org.apache.http.protocol.HttpContext)
     */
    @Override
    public void handle(HttpRequest req, HttpResponse rsp, NHttpResponseTrigger trigger, HttpContext ctx)
            throws HttpException, IOException    {
        tlog().debug("HttpNRequestCB: URI=> {}" , req.getRequestLine().getUri()) ;            

        final HttpIO dev = (HttpIO) getDevice();
        WaitEvent w= new AsyncWaitEvent( createEvent(dev.isSSL(), req, ctx), 
                new NIOHttpTrigger(trigger, this, rsp) );
        final Event ev = w.getInnerEvent();
        
        w.timeoutMillis( dev.getWaitMillis());
        dev.holdEvent(w) ;
        
        getDevice().getDeviceManager().getEngine()
        .getScheduler().run( new Runnable(){
            public void run() {                
                dev.dispatch(ev) ;
            }            
        });
        
    }

    
}
