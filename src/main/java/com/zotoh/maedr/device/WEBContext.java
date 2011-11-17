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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Module;
import com.zotoh.maedr.core.Vars;

public class WEBContext implements ServletContextListener, Vars {
	
    private Logger ilog() {  return _log=getLogger(WEBContext.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
	
    private Device _dev;
    
	/**
	 * 
	 */
	public WEBContext() {
		tlog().debug("WEBContext: ctor()");
	}
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent evt) {
    	
    	tlog().debug("WEBContext: contextInitialized()");
    	
        System.getProperties().put(PIPLINE_MODULE, "com.zotoh.maedr.wflow.FlowModule");
        
        ServletContext x= evt.getServletContext();
        String ctx="";
        int m= x.getMajorVersion(),
        n= x.getMinorVersion();
        
        if (m > 2 || ( m==2 && n > 4)) {
            ctx= x.getContextPath();
        }
        
        try {
            inizAsJ2EE(x, ctx);
        }
        catch (Exception e) {
            tlog().error("", e) ;
            throw new RuntimeException(e);
        }
        
        x.setAttribute(WEBSERVLET_DEVID, _dev);
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent e) {
    	tlog().debug("WEBContext: contextDestroyed()");
        if (_dev != null) try {
            _dev.getDeviceManager().getEngine().shutdown();
        }
        catch (Throwable t) {}
        finally { _dev=null; }        
    }

    private void inizAsJ2EE(ServletContext ctx, String ctxPath) throws Exception {
        File webinf = new File( niceFPath( ctx.getRealPath("/WEB-INF/"))),
        				root= webinf.getParentFile();
        Properties props= new Properties();
        File cfg= new File(root, CFG);
        InputStream inp= readStream( new File(cfg, APPPROPS));
        try { props.load(inp); } finally { close(inp); }
        
        
    	Module<?,?> m= Module.getPipelineModule();
        AppEngine<?,?> eng= m.newEngine();
        eng.startViaServlet(root, props) ;        
        _dev = eng.getDeviceManager().getDevice(WEBSERVLET_DEVID);
        ((Weblet)_dev).setContextPath(ctxPath);
    }
        
}
