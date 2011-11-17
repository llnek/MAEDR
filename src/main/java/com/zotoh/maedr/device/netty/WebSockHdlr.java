/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.WEBSOCKET;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.security.MessageDigest;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.CharsetUtil;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.device.AsyncWaitEvent;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.WaitEvent;

/**
 * @author kenl
 *
 */
public class WebSockHdlr extends SimpleChannelUpstreamHandler {

    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
					throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChannelClosed - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
        _dev.popOneChannel(c);
		super.channelClosed(ctx, e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
					throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChannelConnected - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
		super.channelConnected(ctx, e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChannelDisconnected - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());

		super.channelDisconnected(ctx, e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelOpen(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
					throws Exception {
        Channel c= e.getChannel();
        if (c ==null)  {
            c= ctx.getChannel();
        }
        		
        tlog().debug("NettpReqHdlr: ChannelOpen - ctx {}, channel {}", 
        						ctx, c==null ? "?" : c.toString());
        _dev.pushOneChannel(c);
		super.channelOpen(ctx, e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#childChannelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChildChannelStateEvent)
	 */
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

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#childChannelOpen(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChildChannelStateEvent)
	 */
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

	private Logger ilog() {  return _log=getLogger(WebSockHdlr.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    

    private static final String SEC_WEBSOCKET_ACCEPT="Sec-WebSocket-Accept";
    private static final String SEC_WEBSOCKET_KEY= "Sec-WebSocket-Key";
	private static final int MAX_FRAME_SIZE = 1024 * 16;
    
    private final WebSockIO _dev;
	private final String _pathUri;
	
	/**
	 * @param dev
	 * @param uri
	 */
	protected WebSockHdlr(WebSockIO dev, String uri) {
		_pathUri=uri;
		_dev=dev;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
		Object msg = e.getMessage();		
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}
	
    /* (non-Javadoc)
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
                    throws Exception {
        tlog().error("",e.getCause());
        e.getChannel().close();
    }	

	/**/
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req)
				throws Exception {
		
	    HttpMethod mtd= req.getMethod();
	    String uri= req.getUri();
	    Channel cc;
	    
        tlog().debug("WebSockHdlr: request uri= {}", uri);
        
		if ( ! ( GET == mtd)) {
	        tlog().debug("WebSockHdlr: expecting /GET, got {}", mtd);			
			sendHttpResponse(ctx, req, 
			        new DefaultHttpResponse(HTTP_1_1,	FORBIDDEN));
			return;
		}
		
		if (uri.equals(_pathUri)
				&& Values.UPGRADE.equalsIgnoreCase(req.getHeader(CONNECTION))
				&& WEBSOCKET.equalsIgnoreCase(req.getHeader(Names.UPGRADE))) {
			
			HttpResponse res=null;			
			try {
				res = doHandshake(req, ctx);
			}
			catch (Exception e) {
				tlog().error("",e);
				ctx.getChannel().close();
			}
			
			cc=ctx.getChannel();
			cc.write(res);

//			_chGrp.add(ctx.getChannel());

			// now replace the encoder/decoder with frame encoder/decoder
			
			ChannelPipeline pl = ctx.getChannel().getPipeline();
			pl.remove("aggregator");
			pl.replace("decoder", "wsdecoder", new WebSocketFrameDecoder(MAX_FRAME_SIZE));
			pl.replace("encoder", "wsencoder", new WebSocketFrameEncoder());

			// check if ssl ?
			// get the SslHandler in the current pipeline.
			final SslHandler ssl= ctx.getPipeline().get(SslHandler.class);
			if (ssl != null) {
				ChannelFuture cf= ssl.handshake();
				cf.addListener(new ChannelFutureListener(){
					public void operationComplete(ChannelFuture f) throws Exception {
						if (!f.isSuccess()) {
							f.getChannel().close();
						}						
					}					
				});
			}
			
		} else {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));			
		}

	}
	
	private HttpResponse doHandshake(HttpRequest req, ChannelHandlerContext ctx) 
			throws Exception {
		boolean seckey = req.containsHeader(SEC_WEBSOCKET_KEY);
		String protocol;
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1,	new HttpResponseStatus(101,	
						( seckey?"Upgrading Protocol":"Web Socket Handshake") ));
		
		res.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.UPGRADE);
		res.addHeader(HttpHeaders.Names.UPGRADE, "websocket");
		
		if (req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL)) {
			protocol = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL);			
		} else {
			protocol = req.getHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL);						
		}
		
		if (req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1) 
						&& req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2)) {
			// new handshake method with a challenge:
			res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
			res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation(req));
			
			if (!isEmpty(protocol)) {
				res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, protocol);
			}
			
			// answer the challenge.
			String k1 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1);
			String k2 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2);
			int a = (int) (Long.parseLong(k1.replaceAll("[^0-9]", "")) / k1.replaceAll("[^ ]", "").length());
			int b = (int) (Long.parseLong(k2.replaceAll("[^0-9]", "")) / k2.replaceAll("[^ ]", "").length());
			long c = req.getContent().readLong();
			ChannelBuffer bi = ChannelBuffers.buffer(16);
			bi.writeInt(a);
			bi.writeInt(b);
			bi.writeLong(c);
			ChannelBuffer bo = ChannelBuffers.wrappedBuffer(MessageDigest.getInstance("MD5").digest(bi.array()));
			res.setContent(bo);
		} 
		else if (seckey){
			//version 14, http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-14			
			res.addHeader(SEC_WEBSOCKET_ACCEPT, NettpHplr.calcHybiSecKeyAccept( req.getHeader(SEC_WEBSOCKET_KEY) ));
			if (!isEmpty(protocol)) {
				res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, protocol);
			}
			
		} else {
			// older handshake method with no challenge:
			res.addHeader(HttpHeaders.Names.WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
			res.addHeader(HttpHeaders.Names.WEBSOCKET_LOCATION, getWebSocketLocation(req));
			if (!isEmpty(protocol)) {
				res.addHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL, protocol);
			}
		}
		return res;
	}

	
	/**/
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
					WebSocketFrame frame) {
		
		if ( frame.isBinary()) {
			frame.getBinaryData();
		} else {
			frame.getTextData();
		}
		
        WaitEvent w= new AsyncWaitEvent( new WebSockEvent(_dev, frame),                
                new WebSockTrigger(_dev, ctx) );
        final Event ev = w.getInnerEvent();
        
        w.timeoutMillis(_dev.getWaitMillis());
        _dev.holdEvent(w) ;
        
        _dev.getDeviceManager().getEngine()
        .getScheduler().run( new Runnable(){
            public void run() {                
                _dev.dispatch(ev) ;
            }            
        });
		
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req,
					HttpResponse res) {
		// generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus()
							.toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + _pathUri;
	}


}
