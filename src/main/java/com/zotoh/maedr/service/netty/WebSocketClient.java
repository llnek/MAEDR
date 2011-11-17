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

package com.zotoh.maedr.service.netty;

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.net.InetSocketAddress;
import java.net.URI;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.service.WebSocketError;


/**
 * @author kenl
 *
 */
public abstract class WebSocketClient extends SimpleChannelUpstreamHandler {
   
    private Logger ilog() {  return _log=getLogger(WebSocketClient.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    protected WebSocketClientCB _cb;
    private boolean _handskDone;
    private Channel _ch;
    protected URI _remote;
    protected ClientBootstrap _bs;

    /**
     * 
     */
    protected WebSocketClient() {
    		iniz();
    }

    /**
     * 
     */
    protected abstract void iniz() ;
    
    /**
     * @throws Exception
     */
    public void start() throws Exception {
//        String s = _remote.getScheme();
        final WebSocketClient me=this;
        
//        if (s.equals("ws") || s.equals("wss")) {} else {
//            errBadArg("Unsupported protocol: " + s);
//        }
                
        _bs.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pl = Channels.pipeline();
                pl.addLast("decoder", new HttpResponseDecoder());
                pl.addLast("encoder", new HttpRequestEncoder());
                pl.addLast("ws-handler", me);
                return pl;
            }
        });

        _bs.setOption("tcpNoDelay" , true);
        _bs.setOption("keepAlive", true);

        connect();
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	
        String path = _remote.getPath(),
                scheme= _remote.getScheme(),
                qry= _remote.getQuery();
        
        if ( ! isEmpty(qry)) {
            path = _remote.getPath() + "?" + qry;
        }

        _ch = e.getChannel();

        HttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        req.addHeader(Names.ORIGIN, scheme+"://" + _remote.getHost());
        req.addHeader(Names.HOST, _remote.getHost());
        req.addHeader(Names.UPGRADE, Values.WEBSOCKET);
        req.addHeader(Names.CONNECTION, Values.UPGRADE);
        _ch.write(req);
        
        // after handshake, put in the websock stuff
        ctx.getPipeline().replace("encoder", "ws-encoder", new WebSocketFrameEncoder());
    }

    /**
     * 
     */
    public void stop() {
    		disconnect();
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (_cb != null) { _cb.onDisconnect(this); }
        reset();
    }

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg= e.getMessage();
        
        if ( !_handskDone) {
            maybeCheckHandshake(ctx, msg);
        } 
        else if (msg instanceof WebSocketFrame) {
            if (_cb != null) { _cb.onFrame( this, (WebSocketFrame) msg); }            
        }
        else if (msg instanceof HttpResponse) {
            HttpResponseStatus s = ( (HttpResponse) msg).getStatus();
            throw new WebSocketError("Unexpected HttpResponse: " 
                    + s.getCode() + ", " + s.getReasonPhrase());            
        }
        else {
            throw new WebSocketError("Expecting websocket-frame, got " + msg.getClass().getName());        
        }
    }

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable t= e.getCause();
        
        tlog().error("", t);
        
        if (_cb != null) { _cb.onError(this, t); }    
        e.getChannel().close();
    }

    /**
     * @return
     */
    public ChannelFuture connect() {
        return _bs.connect(new InetSocketAddress(_remote.getHost(), _remote.getPort()));
    }

    /**
     * @return
     */
    public ChannelFuture disconnect() {
        try { return _ch.close(); } finally { _ch=null; }
    }
        

    /**
     * @param frame
     * @return
     */
    public ChannelFuture send(WebSocketFrame frame) {
        return _ch.write(frame);
    }

    /**
     * @return
     */
    public URI getUrl() {        return _remote;    }

    private void reset() {        _handskDone=false;    }

    private void maybeCheckHandshake(ChannelHandlerContext ctx, Object msg) throws Exception {
//          new HttpResponseStatus(101, "Web Socket Protocol Handshake");
        HttpResponse res = (HttpResponse)msg;

        if ( res.getStatus().getCode() != 101
              || !Values.WEBSOCKET.equalsIgnoreCase(res.getHeader(Names.UPGRADE))
          || !Values.UPGRADE.equalsIgnoreCase(res.getHeader(Names.CONNECTION))) {
          throw new WebSocketError("Invalid handshake");                
        }
      
        _handskDone=true;
        ctx.getPipeline().replace("decoder", "ws-decoder", new WebSocketFrameDecoder());
        if (_cb != null) { _cb.onConnect(this); }
    }
    
}
