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

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.StrUte.trim;
import static org.jboss.netty.channel.Channels.pipeline;

import java.util.Properties;
import java.util.ResourceBundle;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.maedr.device.DeviceManager;

/**
 * A HTTP device implementing the WebSocket protocol via Jboss/netty.
 * 
 * The set of properties:
 * 
 * <b>uri</b>
 * The Http URI request path.
 * 
 * @see com.zotoh.maedr.device.NettyIOTrait
 * 
 * @author kenl
 * 
 */
public class WebSockIO extends NettyIOTrait {

	private String _pathUri;
       
    /**
     * @param mgr
     */
    public WebSockIO(DeviceManager<?,?> mgr) {
        super(mgr);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.BaseHttpIO#inizWithProperties(org.json.JSONObject)
     */
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception {
		super.inizWithProperties(deviceProperties) ;
		
        String cpath= trim(deviceProperties.optString("uri"));        
        tstEStrArg("uri-path", cpath) ;        
        _pathUri= cpath;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    @Override
    protected void onStart() throws Exception {
        ServerBootstrap boot = onStart_0();        
        final WebSockIO dev=this;
        boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();
                dev.maybeCfgSSL( pipeline);                
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", new WebSockHdlr(dev,_pathUri));
                return pipeline;
            }            
        });
        
        onStart_1(boot);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props)
    throws Exception {
        final CmdLineQuestion q1= new CmdLineMandatory("uri", getResourceStr(rcb, "cmd.uri.fmt")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("uri", answer);
                return "";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props), q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    
}
