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

import static com.zotoh.core.util.ProcessUte.asyncExec;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.ssl.SslHandler;

import com.zotoh.core.util.GUID;
import com.zotoh.core.util.Tuple;
import com.zotoh.maedr.device.BaseHttpIO;
import com.zotoh.maedr.device.DeviceManager;

/**
 * Base class for Http IO devices based on Jboss/nettty.
 * 
 * The set of properties:
 * 
 * @see com.zotoh.maedr.device.BaseHttpIO
 * 
 * @author kenl
 * 
 */
public abstract class NettyIOTrait extends BaseHttpIO {

    private ServerSocketChannelFactory _fac;
    private ChannelGroup _chGrp;
    	
    /**
     * @param mgr
     * @param ssl
     */
    protected NettyIOTrait(DeviceManager<?,?> mgr, boolean ssl)     {
        super(mgr, ssl); 
    }
    
    /**
     * @param mgr
     */
    protected NettyIOTrait(DeviceManager<?,?> mgr)     {
        this(mgr, false);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.BaseHttpIO#isAsync()
     */
    public boolean isAsync() {        return true;    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    @Override
    protected void onStop()     {

        final ServerSocketChannelFactory f= _fac;
        final ChannelGroup g= _chGrp;
        
        asyncExec(new Runnable(){
            public void run() {    
                try {
                    g.close().awaitUninterruptibly();
                    f.releaseExternalResources();
                }
                catch (Throwable t) {}
                return;
            }
        });
    }

    /**
     * @param c
     */
    protected void pushOneChannel(Channel c) {
        if (_chGrp != null && c != null)
        { _chGrp.add(c); }
    }
    
    /**
     * @param c
     */
    protected void popOneChannel(Channel c) {
        if (_chGrp != null && c != null)
        { _chGrp.remove(c) ; }
    }
    
    /**
     * @param pipeline
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
    protected void maybeCfgSSL(ChannelPipeline pipeline) 
		    throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableEntryException, 
		    KeyStoreException, CertificateException, IOException {
    	
        if (! isSSL()) { return ; }
      
        Tuple t= cfgSSL(true, getSSLType(), getKeyURL(), getKeyPwd());      
        SSLContext c= (SSLContext) t.get(1);      
        SSLEngine engine = c.createSSLEngine();
        engine.setUseClientMode(false);        
        pipeline.addLast("ssl", new SslHandler(engine) );      
    }

    /**
     * @return
     * @throws Exception
     */
    protected ServerBootstrap onStart_0() throws Exception {
        _chGrp= new DefaultChannelGroup(GUID.generate()); 
        _fac= new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());        
        return new ServerBootstrap(_fac);    	
    }

    /**
     * @param boot
     * @throws Exception
     */
    protected void onStart_1(ServerBootstrap boot) throws Exception {
    	Channel c;
    	
        boot.setOption("reuseAddress", true);
        // Options for its children
        boot.setOption("child.tcpNoDelay", true);
        boot.setOption("child.receiveBufferSize", 1024*1024); // 1MEG
        c=boot.bind(new InetSocketAddress( getIP(), getPort()));
        
//        c.getConfig().setConnectTimeoutMillis(millis);
        _chGrp.add(c);
    }
    
}
