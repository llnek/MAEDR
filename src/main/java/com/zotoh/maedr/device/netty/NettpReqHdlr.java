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

import static com.zotoh.core.util.CoreUte.safeGetClzname;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.IOException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.device.BaseHttpIO.NIOCB;

/**
 * For netty IO.
 * 
 * @author kenl
 */
public class NettpReqHdlr extends SimpleChannelHandler {
    
    private Logger ilog() {  return _log=getLogger(NettpReqHdlr.class);    }
    private transient Logger _log= ilog();    
    private final NettpIO _dev;
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    
    /**
     * @param dev
     */
    public NettpReqHdlr(NettpIO dev) {
        _dev= dev;
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelHandler#channelOpen(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)  
			throws Exception { 
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: channelOpen - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
        
        _dev.pushOneChannel( c );
        
        super.channelOpen(ctx, e) ;
    }

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
    				throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: channelDisconnected - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
    		super.channelConnected(ctx, e);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
    				throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
    		
        tlog().debug("NettpReqHdlr: channelConnected - ctx {}, channel {}", 
    						ctx, c==null ? "?" : c.toString());
    		
    		super.channelConnected(ctx, e);
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
    				throws Exception {        
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        
		tlog().debug("NettpReqHdlr: channelClosed - ctx {}, channel {}", 
						ctx, c==null ? "?" : c.toString());
		
        _dev.removeCB(c) ;                    
        super.channelClosed(ctx, e) ;
    }
    
    /**/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
    				throws Exception {        
        Channel c= e.getChannel();
        NIOCB cb;
        if (c== null) {
            c= ctx.getChannel();
        }
        
        tlog().error("", e.getCause()) ;
        
        if (c != null) {
            
            cb=  _dev.removeCB(c) ;
            if (cb != null) {
                cb.destroy();
            }
            
            c.close();                    
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev) 
    				throws Exception {        
        Object msg = ev.getMessage();
        Channel c= ctx.getChannel();
        NettpMsgCB cb= null;
        
        if (msg instanceof HttpRequest) {
            _dev.addCB(c, cb= new NettpMsgCB(_dev));
            cb.onREQ(ctx,ev);
        }
        else
        if (msg instanceof HttpChunk) {
            cb= (NettpMsgCB) _dev.getCB(c);
            if (cb != null) {
                cb.onChunk(ctx,ev) ;
            }
            else {
                throw new IOException("NettpReqHdlr:  failed to reconcile http-chunked msg") ;                
            }
        }
        else {
            throw new IOException("NettpReqHdlr:  unexpected msg type: " + safeGetClzname(msg)) ;                
        }

    }

	@Override
	public void childChannelClosed(ChannelHandlerContext ctx,
					ChildChannelStateEvent e) throws Exception {
		
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChildChannelClosed - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
        _dev.popOneChannel(c);
		super.childChannelClosed(ctx, e);
	}

	@Override
	public void childChannelOpen(ChannelHandlerContext ctx,
					ChildChannelStateEvent e) throws Exception {
		
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChildChannelOpen - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
        _dev.pushOneChannel(c);
		super.childChannelOpen(ctx, e);
	}
    
    
}
