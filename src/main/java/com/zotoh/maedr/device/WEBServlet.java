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
 
package com.zotoh.maedr.device;

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.MetaUte.loadClass;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.Vars;
import com.zotoh.netio.HTTPStatus;

/**
 * @author kenl
 *
 */
public class WEBServlet extends HttpServlet implements Vars {
	
	private static final long serialVersionUID = -3862652820921092885L;
	
    private Logger ilog() {  return _log=getLogger(WEBServlet.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
	
    private boolean _jettyAsync=false;
    private BaseHttpIO _dev;
    
    /**
     * @param dev
     */
    public WEBServlet(BaseHttpIO dev) {
		tlog().debug("WEBServlet: ctor(dev)");
    	_dev=dev;
    }
    
	/**
	 * 
	 */
	public WEBServlet() {
		tlog().debug("WEBServlet: ctor()");
	}
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
    	tlog().debug("WEBServlet: destroy()");
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    public void service(ServletRequest request, ServletResponse response) 
    	            throws ServletException, IOException    {    	    	
		HttpServletResponse rsp= (HttpServletResponse) response;
		HttpServletRequest req= (HttpServletRequest) request;
        HttpEvent evt;         		
		
        tlog().debug("{}\n{}\n{}",
				"********************************************************************",
		        req.getRequestURL(),
		        "********************************************************************");
        
        evt= (HttpEvent) HttpHplr.extract( _dev, req);
		if (_jettyAsync) {
			doASyncSvc(evt, req, rsp);
		} else {
			doSyncSvc(evt, req,rsp);
		}
		
    }
    
    private void doASyncSvc(HttpEvent evt, HttpServletRequest req, HttpServletResponse rsp) {
        Continuation c = ContinuationSupport.getContinuation(req);
        if (c.isInitial())  {
            try {
                dispREQ(c, evt, req,rsp);
            }
            catch (Exception e) {
                tlog().error("",e);
            }
        }    	
    }

    private void doSyncSvc(HttpEvent evt, HttpServletRequest req, HttpServletResponse rsp) {
        WaitEvent w= new SyncWaitEvent( evt );
        final Event ev = w.getInnerEvent();
		
        _dev.holdEvent(w) ;  
        
        _dev.getDeviceManager().getEngine()
        .getScheduler().run( new Runnable(){
            public void run()  {                
                _dev.dispatch(ev) ;
            }            
        });
        
        try {
            w.timeoutMillis(  _dev .getWaitMillis());
        }
        finally {
            _dev.releaseEvent(w);
        }
        
        EventResult res= w.getInnerEvent().getResult();
        
        if (res instanceof HttpEventResult)        {
            replyService( (HttpEventResult) res, rsp);
        } else {
            replyService( new HttpEventResult(HTTPStatus.REQUEST_TIMEOUT), rsp);
        }
                    	
    }
    
    protected void replyService(HttpEventResult res, HttpServletResponse rsp)  {        
        
        Map<String,String> hdrs= res.getHeaders();
        StreamData data  = res.getData();
        long clen=0L;
        int sc= res.getStatusCode();
        
        try  {            
            
        	for (String n : hdrs.keySet())            {
                if ( "content-length".equalsIgnoreCase(n))    {}
                else
                { rsp.setHeader(n, hdrs.get(n)) ; }
            }
        	
            if (data != null && data.hasContent()) {
            	clen=data.getSize();
            }
            
            rsp.setContentLength( new Long(clen).intValue() );
            
        	if (res.hasError()) {
        		rsp.sendError(sc, res.getErrorMsg());
        	} else {
        		rsp.setStatus(sc) ;
        	}
        	            
            if (data != null && data.hasContent()) {
            	StreamUte.streamToStream( data.getStream() , rsp.getOutputStream(),  clen);
            }
            
        }
        catch (Exception e) {        
            tlog().warn("",e);
        }
        
    }
    
    
    /**/
    private void dispREQ(Continuation ct, HttpEvent evt, HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        ct.setTimeout(_dev.getWaitMillis()) ;
        ct.suspend(rsp);
        
        WaitEvent w= new AsyncWaitEvent( evt,  new JettyAsyncTrigger(_dev, req, rsp) );
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
        
    public void init(ServletConfig config) throws ServletException    {        
        super.init(config);
        
        ServletContext ctx= config.getServletContext();
        Object obj= ctx.getAttribute(WEBSERVLET_DEVID);
        
        if (obj instanceof BaseHttpIO) {
        	_dev= (BaseHttpIO) obj;
        }
        
    	try { 
    		Class<?> z= loadClass("org.eclipse.jetty.continuation.ContinuationSupport");
    		if (z != null) { _jettyAsync= true; }    		
    	} catch (Throwable t) {}
    	
    	try {
    	    tlog().debug("{}\n{}{}\n{}\n{}{}", 
                "********************************************************************",
                "Servlet Container: ",
                ctx.getServerInfo(),
                "********************************************************************",
                "Servlet:iniz() - servlet:" , 
                getServletName());
    	}
    	catch (Throwable t)
    	{}
    }
    
}
