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

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.HttpEvent;

/**
 * Http Device using JBoss-Netty which is asynchronous by design. 
 * 
 * The set of properties:
 * 
 * @see com.zotoh.maedr.device.NettyIOTrait
 * 
 * @author kenl
 */
public class NettpIO extends NettyIOTrait {
    
    /**
     * @param mgr
     * @param ssl
     */
    public NettpIO(DeviceManager<?,?> mgr, boolean ssl)     {
        super(mgr, ssl); 
    }
    
    /**
     * @param mgr
     */
    public NettpIO(DeviceManager<?,?> mgr)     {
        this(mgr, false);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    @Override
    protected void onStart() throws Exception     {
        ServerBootstrap boot = onStart_0();
        final NettpIO dev= this;   

        // from netty examples/tutorials...
        boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();                
                dev.maybeCfgSSL( pipeline);
                pipeline.addLast("decoder", new HttpRequestDecoder());
//                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));                
                pipeline.addLast("encoder", new HttpResponseEncoder());
                //pipeline.addLast("deflater", new HttpContentCompressor());
                pipeline.addLast("chunker", new ChunkedWriteHandler());
                pipeline.addLast("handler", dev.getHandler());
                return pipeline;
            }
        });

        onStart_1(boot);
    }

    /**
     * @return
     */
    protected SimpleChannelHandler getHandler() {    		return new NettpReqHdlr(this);    }
    
    /**
     * @return
     */
    protected HttpEvent createEvent() {            return new HttpEvent(this);    }
    
}

