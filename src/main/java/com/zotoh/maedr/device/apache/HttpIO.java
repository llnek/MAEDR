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

import static com.zotoh.core.util.ProcessUte.asyncExec;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.ssl.SSLServerIOEventDispatch;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.protocol.AsyncNHttpServiceHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.NHttpRequestHandlerRegistry;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactor;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;
import com.zotoh.maedr.device.BaseHttpIO;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.netio.NetUte;

/**
 * HTTP server socket using Apache Httpcomponents.
 * 
 * The set of properties:
 * 
 * @see com.zotoh.maedr.device.BaseHttpIO
 * 
 * @author kenl
 */
public class HttpIO extends BaseHttpIO  {
    
    private IOReactor _curIO;
    
    /**
     * @param mgr
     * @param ssl
     */
    public HttpIO( DeviceManager<?,?> mgr, boolean ssl ) {
        super(mgr, ssl);
    }

    /**
     * @param mgr
     */
    public HttpIO(DeviceManager<?,?> mgr) {
        this( mgr, false) ;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    protected void onStart() throws Exception {
    	
        // mostly copied from apache http tutotial...
        
    	HttpParams params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, (int)getSocetTimeoutMills() )
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)	// 8k?
            .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
            .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
            .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "Apache-HttpCore/4.x");

        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());
        
        ConnectionReuseStrategy strategy= new DefaultConnectionReuseStrategy();
        HttpResponseFactory rspFac= new DefaultHttpResponseFactory();        
        NHttpServiceHandler svc;
        EventListener evt= new EventLogger(getId(), tlog());
        
        if ( isAsync()) {
            AsyncNHttpServiceHandler handler = new AsyncNHttpServiceHandler(  httpproc, rspFac, strategy, params);
            NHttpRequestHandlerRegistry r = new NHttpRequestHandlerRegistry();
            r.register("*", new HttpNRequestCB(this)) ;
            handler.setHandlerResolver(r) ;
            handler.setEventListener(evt) ;
            svc= handler;
        }   else {
            StreamedHttpServiceHandler handler = new StreamedHttpServiceHandler(  httpproc, rspFac, strategy, params);
            HttpRequestHandlerRegistry r = new HttpRequestHandlerRegistry();
            r.register("*", new HttpRequestCB(this));            
            handler.setHandlerResolver(r) ;
            handler.setEventListener(evt) ;
            svc= handler;
        }

        IOEventDispatch disp= isSSL() ? onSSL(svc, params) : onBasic(svc, params); 
        ListeningIOReactor ioReactor;        
        ioReactor = new DefaultListeningIOReactor( getWorkers(), params);
        ioReactor.listen(new InetSocketAddress(
                NetUte.getNetAddr( getHost()) , getPort() ));        
        _curIO= ioReactor;
        
        // start...
        runServer(disp, ioReactor) ;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    protected void onStop() {
        
        try {
            if (_curIO != null) {      _curIO.shutdown() ;        }
        }
        catch (Exception e)        {
            tlog().warn("", e) ;
        }
        finally {
        	_curIO=null;
        }
        
    }
    
    /**/
    private void runServer(final IOEventDispatch disp,
            final ListeningIOReactor listener) throws Exception    {
        
        asyncExec ( new Runnable() {
            public void run()   {
                    try {
                        listener.execute(disp);
                    }
                    catch (IOException e)   {
                        tlog().error("", e) ;
                    }                
                    return;                
            }            
        });
        
    }
    
    /**/
    private IOEventDispatch onSSL(NHttpServiceHandler svc, HttpParams params) 
    throws Exception {                
		Tuple t= cfgSSL(true, getSSLType(), getKeyURL(), getKeyPwd());
        return new SSLServerIOEventDispatch(svc, (SSLContext) t.get(1), params);
    }
    
    /**/
    private IOEventDispatch onBasic(NHttpServiceHandler svc, HttpParams params) 
    throws Exception {        
         return new DefaultServerIOEventDispatch( svc, params);
    }
    
}

/**/
class EventLogger implements EventListener {

    public void connectionOpen(final NHttpConnection conn) {
        _log.debug("{}{}", _source , ": connection open()");
    }

    public void connectionTimeout(final NHttpConnection conn) {
        _log.debug("{}{}",_source, ": connection timed out");
    }

    public void connectionClosed(final NHttpConnection conn) {
        _log.debug("{}{}",_source , ": connection closed");
    }

    public void fatalIOException(final IOException ex, final NHttpConnection conn) {
        _log.debug("{}{}",_source , ": fatal IOException");
        _log.error("", ex) ;
    }

    public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
        _log.debug("{}{}",_source , ": fatal ProtocolException");
        _log.error("", ex) ;
    }
    
    public EventLogger(String id, Logger log)    {
        _source=id; _log=log;
    }
    
    private String _source;
    private Logger _log;
}
    
