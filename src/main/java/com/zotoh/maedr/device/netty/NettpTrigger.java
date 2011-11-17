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


package com.zotoh.maedr.device.netty;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.InputStream;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.stream.ChunkedStream;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.maedr.device.AsyncTrigger;
import com.zotoh.maedr.device.EventResult;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.netio.HTTPStatus;

/**
 * Triggers a response back to client - netty IO.
 * 
 * @author kenl
 */
class NettpTrigger  extends AsyncTrigger  {

    private final MessageEvent _msgEvt;
    private final NettpMsgCB _cb;
    
    /**
     * @param cb
     * @param e
     */
    public NettpTrigger( NettpMsgCB cb, MessageEvent e) {
    	super(cb==null ? null : cb.getDevice());
        _cb=cb;
        _msgEvt= e;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithResult(com.zotoh.maedr.device.EventResult)
     */
    @Override
    public void resumeWithResult(EventResult rs)    {

        HttpEventResult res= (HttpEventResult) rs;
    
        try {            
            reply(res);
        }
        catch (Exception e) {
            _cb.tlog().error("", e) ;
        }
        
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithError()
     */
    @Override
    public void resumeWithError()    {        
        resumeWithResult(  new HttpEventResult(HTTPStatus.INTERNAL_SERVER_ERROR) );
    }
    
    private void reply(HttpEventResult res) throws Exception {
        
        HttpResponse rsp = new DefaultHttpResponse(HTTP_1_1, 
                    new HttpResponseStatus(res.getStatusCode(), res.getStatusText() ));
        
        StreamData data= res.getData();
        byte[] bits= null;
        long clen = 0L;
        InputStream inp= null;
        ChannelFuture cf=null;        
        Channel ch= _msgEvt.getChannel();
        
        for (Map.Entry<String,String> hdrs :  res.getHeaders().entrySet()) {
            rsp.setHeader( hdrs.getKey(), hdrs.getValue() );
        }
        
        if (data != null && data.hasContent() ) {

            if ( data.isDiskFile() ) {
                clen= data.getSize();
            }
            else {
                bits= data.getBytes();
            }
            if (bits != null) {
                clen = (long) bits.length;
            }
        }

        rsp.setHeader("content-length", Long.toString(clen)) ;
        if (clen > 0L) {
            if (bits != null) {
                inp= StreamUte.asStream(bits) ;
            }
            else {
                inp= data.getStream();
            }
        }
        
        _cb.setCookie(rsp);        
        
        //TODO: this throw NPE some times !
        try { cf= ch.write(rsp); } catch (Exception e) {
            tlog().error("",e);
        }
        
        if (inp != null) {
            final boolean keepAlive= _cb.keepAlive();
            final InputStream _in= inp;            
            cf= ch.write(new ChunkedStream(inp));   
            cf.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    StreamUte.close(_in);
                    if (!keepAlive) {
                        future.addListener(ChannelFutureListener.CLOSE);
                    }
                }
                public void operationProgressed(
                        ChannelFuture future, long amount, long current, long total) {}
            });            
        }
        else {
            _cb.preEnd(cf) ;
        }
        
//        if (kalive) {
//            // Add 'Content-Length' header only for a keep-alive connection.
//            rsp.setHeader("content-length", clen.toString());
//        }
        
    }
    
        
}

