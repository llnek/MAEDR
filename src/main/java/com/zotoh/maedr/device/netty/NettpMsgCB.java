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

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;
import com.zotoh.maedr.device.AsyncWaitEvent;
import com.zotoh.maedr.device.BaseHttpIO.NIOCB;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.WaitEvent;

/**
 * Callback used to work with the netty asynchronous IO framework.
 *
 * @author kenl
 */
class NettpMsgCB extends NIOCB {
    
    private Logger ilog() {  return _log=getLogger(NettpMsgCB.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private transient OutputStream _os;
    private long _clen;
    private HttpRequest _request;
    private boolean _keepAlive;
    private final NettpIO _dev;    
    
    private HttpEvent _event;
    private File _fout;
    private CookieEncoder _cookie;
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.NIOCB#destroy()
     */
    public void destroy()     {
        _os= StreamUte.close(_os);
        _fout=null;
        _request= null;
        _event= null;
        _cookie= null;
    }
    
    /**
     * @return
     */
    public boolean keepAlive() {        return _keepAlive;    }
    
    /**
     * @return
     */
    public CookieEncoder getCookie() {        return _cookie;    }
    
    /**
     * @return
     */
    public Device getDevice() { return _dev; }
    
    /**
     * @param dev
     */
    protected NettpMsgCB(NettpIO dev) {
        _dev=dev;
    }
       
    /**
     * @param rsp
     */
    protected void setCookie(HttpResponse rsp) {
        if (_cookie != null) {
            rsp.addHeader(SET_COOKIE, _cookie.encode());            
        }
    }
    
    /**
     * @return
     */
    protected HttpEvent getEvent() {        return _event;    }
    
    /**
     * @param future
     */
    protected void preEnd(ChannelFuture future) {
        // Close the non-keep-alive connection after the write operation is done.
        if ( ! _keepAlive ) {
            future.addListener(ChannelFutureListener.CLOSE);
        }        
    }
    
    /**
     * @param ctx
     * @param e
     * @throws Exception
     */
    protected void onREQ(ChannelHandlerContext ctx, MessageEvent ev) 
    				throws Exception {        
        if (this._request != null) {
            throw new Exception("NettpReqHdlr: onREQ: expected to be called once only") ;
        }       
        
        this._request = (HttpRequest) ev.getMessage();        
        tlog().debug("NettpReqHdlr: URI=> {}" , _request.getUri() ) ;            
        
        _event= NettpHplr.extract(_dev, _request);
        _event.setSSL( _dev.isSSL()) ;
        _keepAlive = isKeepAlive( _request);
        
        if (_request.isChunked()) {
            tlog().debug("NettpReqHdlr: request is chunked");
        } else {
            sockBytes(_request.getContent());
            onMsgFinal(ev);                	
        }
    }
    
    /**
     * @param ctx
     * @param ev
     * @throws Exception
     */
    protected void onChunk( ChannelHandlerContext ctx, MessageEvent ev) 
    throws Exception {        
        HttpChunk chunk = (HttpChunk) ev.getMessage();   
//        HttpChunkTrailer trailer;        
		sockBytes(chunk.getContent());
        if (chunk.isLast()) {                    
            onMsgFinal(ev);
        } 
    }
        
    /**/
    private void onMsgFinal(MessageEvent ev) throws IOException {        
        StreamData data= new StreamData();
        if (_fout != null) {
            data.resetMsgContent(_fout) ;
        }
        else
        if (_os instanceof ByteArrayOutputStream)        {            
            data.resetMsgContent(_os) ;
        }
        
        _os=StreamUte.close(_os);
        _event.setData(data) ;
        
        // Encode the cookie.
        String cookie = _request.getHeader(COOKIE);
        if ( ! isEmpty(cookie)) {
            Set<Cookie> cookies = new CookieDecoder().decode(cookie);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder enc = new CookieEncoder(true);
                for (Cookie c : cookies) {
                    enc.addCookie(c);
                }
                _cookie= enc;
            }
        }
        
        final NettpMsgCB me= this;
        final NettpIO nio= _dev;        
        WaitEvent w= new AsyncWaitEvent( _event, 
                new NettpTrigger(me, ev) );
        final Event evt = w.getInnerEvent();
        
        w.timeoutMillis( _dev.getWaitMillis());
        _dev.holdEvent(w) ;
        
        _dev.getDeviceManager().getEngine()
        .getScheduler().run( new Runnable(){
            public void run() {                
                nio.dispatch(evt) ;
            }            
        });
        
    }
    
    
    private void sockBytes(ChannelBuffer cb) throws Exception {
        int c;
        if (cb != null) while ( (c=cb.readableBytes() ) > 0) {
            sockit_down(cb, c);
        }
    }
    
    private void sockit_down(ChannelBuffer cb, int count) throws Exception {
        
        long thold= _dev.getThreshold();
    	
        byte[] bits= new byte[4096] ;
        int len, total=count;
        
        while (total > 0) {
            len = Math.min(4096, total) ;
            cb.readBytes(bits, 0, len) ;
            _os.write(bits, 0, len) ;
            total = total-len;
        }
        
        _os.flush();
        
        if (_clen >= 0L) { _clen += count; }

        if (_clen > 0L && _clen > thold) {
    		swap();
        }
    }
    
    private void swap() throws Exception {
    	ByteArrayOutputStream baos= (ByteArrayOutputStream) _os;
    	Tuple t= StreamUte.createTempFile(true);
    	OutputStream os= (OutputStream) t.get(1);
    	os.write(baos.toByteArray());
    	os.flush();
    	_os=os;
    	_clen= -1L;
    	_fout= (File)t.get(0);
    }
    
    
    
}

