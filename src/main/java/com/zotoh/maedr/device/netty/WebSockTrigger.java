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

import static com.zotoh.core.util.StrUte.nsb;

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ByteBufferBackedChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;

import com.zotoh.maedr.device.AsyncTrigger;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.EventResult;
import com.zotoh.maedr.device.WebSockResult;

/**
 * @author kenl
 *
 */
public class WebSockTrigger extends AsyncTrigger {

    private final ChannelHandlerContext _ctx;
    
    /**
     * @param dev
     * @param ctx
     */
    public WebSockTrigger(Device dev, ChannelHandlerContext ctx) {
        super(dev);
        _ctx=ctx;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithResult(com.zotoh.maedr.device.EventResult)
     */
    @Override
    public void resumeWithResult(EventResult result) {
        WebSockResult res= (WebSockResult) result;
        DefaultWebSocketFrame f=null;        
        if (res.isBinary()) {            
            f= new DefaultWebSocketFrame(0, 
                    new ByteBufferBackedChannelBuffer( ByteBuffer.wrap(res.getBinary())));
        } else {
            f= new DefaultWebSocketFrame(nsb(res.getText())) ;
        }        
        _ctx.getChannel().write(f);
    }

    @Override
    public void resumeWithError() {
        //TODO
    }

}
