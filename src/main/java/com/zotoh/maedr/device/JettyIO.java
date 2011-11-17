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

import static com.zotoh.core.util.CoreUte.asList;
import static com.zotoh.core.util.CoreUte.getCZldr;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;

import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.Tuple;

/**
 * Http IO using Jetty as an embedded web server, using Jetty's continuation to create asynchronicity when handling requests.
 * 
 * The set of properties:
 * 
 * <b>contextpath</b>
 * The application context path.
 * <b>waitmillis</b>
 * The time this request will be put on hold until a result is ready from the downstream application - default is 5 mins.
 * <b>workers</b>
 * The number of worker threads allocated to Jetty, default is 10.
 * <b>warfile</b>
 * The full path pointing to a web WAR file.  The WAR application must use the servlet provided by this framework.
 * If a WAR is defined, then the following properties are ignored. 
 * <b>resbase</b>
 * The full path pointing to the resource base directory.
 * <b>urlpatterns</b>
 * A list of servlet path patterns.
 * <b>filters</b>
 * A list of Filter definitions.  Each Filter definition is another Map of name-value pairs, such as:
 * ----> <b>urlpattern</b> the filter path.
 * ----> <b>class</b> the class for the filter object.
 * ----> <b>params</b> a map of parameters for this filter (key-values).
 * 
 * @see com.zotoh.maedr.device.HttpIOTrait
 * 
 * @author kenl
 * 
 */
public class JettyIO extends BaseHttpIO implements Weblet {
    
//    private static final SMap<JettyIO> _devMap= new SMap<JettyIO>();
    private String _warPtr, _resDir, _logDir, _contextPath;
    private List<Tuple> _filters;
    private List<String> _urls;
    private Server _jetty;
    
    /**
     * @param mgr
     */
    public JettyIO(DeviceManager<?,?> mgr) {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Weblet#setContextPath(java.lang.String)
     */
    public void setContextPath(String path) {
        _contextPath=nsb(path);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Weblet#getContextPath()
     */
    public String getContextPath() {
        return _contextPath;
    }
    
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#inizWithProperties(org.json.JSONObject)
     */
    @SuppressWarnings("serial")
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception {
        super.inizWithProperties(deviceProperties);
        
        String cpath= trim(deviceProperties.optString("contextpath"));        
        _logDir= trim(deviceProperties.optString("logdir"));        
        _resDir= trim(deviceProperties.optString("resbase"));        
        _warPtr= trim(deviceProperties.optString("warfile"));                        
        _contextPath= isEmpty(cpath) ? "" : cpath;

        if (isEmpty(_warPtr)) {
            final JSONArray arr= deviceProperties.optJSONArray("urlpatterns");
            tstObjArg("servlet-url-patterns", arr);
            _urls= new ArrayList<String>() {{ 
                for (int i=0; i < arr.length(); ++i) {
                    add( nsb(arr.get(i)));
                }            
            }};

            final JSONArray fils= deviceProperties.optJSONArray("filters");
            _filters= new ArrayList<Tuple>() {{ 
                if (fils != null) for (int i=0; i < fils.length(); ++i){
                    add ( toFilter( fils.optJSONObject(i)) );
                }            
            }};
       }
        
    }
    
    /**/
    private Tuple toFilter(JSONObject ftr) throws Exception {
        tstObjArg("filter-definition", ftr);
        JSONObject ps= ftr.optJSONObject("params");
        String url=ftr.optString("urlpattern");
        String s, z=ftr.optString("class");
        JSONObject m= new JSONObject();
        tstEStrArg("filter-url-pattern", url);
        tstEStrArg("filter-class", z);
        if (ps != null) {
            for ( Iterator<?> it= ps.keys(); it.hasNext(); ) {
                s= (String) it.next();
                m.put( s, nsb( ps.get(s)));
            }
        }
        return new Tuple(z, url, m);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    @Override
    protected void onStart() throws Exception {
        SelectChannelConnector cc;
        Server svr= new Server();
        if (isSSL()) {
            SslSelectChannelConnector c= new SslSelectChannelConnector();
            Tuple t= cfgSSL(true,getSSLType(), getKeyURL(), getKeyPwd());
            SslContextFactory fac=c.getSslContextFactory();
            fac.setSslContext((SSLContext) t.get(1));
            fac.setWantClientAuth(false);
            fac.setNeedClientAuth(false);
            cc=c;
        }
        else {
            cc = new SelectChannelConnector();
        }
        
        cc.setName(this.getId());
        
        if (!isEmpty(getHost())) {        cc.setHost(getHost());        }
        cc.setPort(getPort());
        
        cc.setThreadPool(new QueuedThreadPool( getWorkers() ));
        cc.setMaxIdleTime(30000);       // from jetty examples
        cc.setRequestHeaderSize(8192);  // from jetty examples
        svr.setConnectors(new Connector[]{ cc});

        if (isEmpty(_warPtr)) {
            onStart_Servlet(svr);
        } else {
            onStart_War(svr);
        }

        _jetty=svr;
        _jetty.start();
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    @Override
    protected void onStop() {
        if (_jetty != null)  
        try { 
            _jetty.stop(); 
        }
        catch (Throwable t) 
        {}
        _jetty=null;
    }

    /**/
    @SuppressWarnings("unchecked")
	private void onStart_Servlet(Server svr) throws Exception {
        //ServletContextHandler x = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.NO_SECURITY);
        ServletContextHandler x = new ServletContextHandler(ServletContextHandler.SESSIONS);
        x.setContextPath(_contextPath);
        x.setClassLoader(getCZldr());
        x.setDisplayName(this.getId());
        x.setResourceBase(isEmpty(_resDir) ? "." : _resDir);

        ContextHandlerCollection contexts = new ContextHandlerCollection();        
        HandlerCollection handlers=  new HandlerCollection();        
        Handler[] hs; 

        /*
        ResourceHandler rh= new ResourceHandler();
        rh.setDirectoriesListed(false);
        rh.setAliases(false);
        rh.setResourceBase(isEmpty(_resDir) ? "." : _resDir);
        rh.setWelcomeFiles(new String[]{ "index.html" });
        */
 
        hs= asList(true, contexts, new DefaultHandler(), maybeREQLog() ).toArray(new Handler[0]) ;
        handlers.setHandlers(hs);

        ServletHolder so=new ServletHolder(new DefaultServlet());
        FilterHolder ho;
        Map<String,String> pms=new HashMap<String,String>();
        //pms.put("org.eclipse.jetty.servlet.Default.dirAllowed","false");
        //pms.put("org.eclipse.jetty.servlet.Default.resourceBase", isEmpty(_resDir)?".":_resDir);
        pms.put("resourceBase", isEmpty(_resDir) ? "." : _resDir);
        so.setInitParameters(pms);
        x.addServlet(so, "/*");                      

        // add url patterns
        for (String u : _urls) {
            so=new ServletHolder(new WEBServlet(this));
            pms= MP();
            pms.put("server-info", Server.getVersion());
            so.setInitParameters(pms);
            x.addServlet(so,u);                      
        }

        // filters
        for (Tuple t : _filters) {
            ho=x.addFilter(nsb(t.get(0)), nsb(t.get(1)), 0 /* Handler.DEFAULT*/);
            ho.setInitParameters( (Map<String,String>) t.get(2));
        }
        
        contexts.addHandler(x);

        svr.setHandler(handlers);               
    }

    /**/
    private void onStart_War(Server svr) throws Exception {
    	
        WebAppContext webapp = new WebAppContext();
        
        webapp.setAttribute("_#version#_", Server.getVersion());        
        webapp.setAttribute("_#device#_", this);
        webapp.setContextPath(_contextPath);
        webapp.setWar(_warPtr);
        webapp.setExtractWAR(true);
        
        svr.setHandler(webapp);
    }

    /**/
    private RequestLogHandler maybeREQLog() throws Exception {
        if (isEmpty(_logDir)) { return null; }
        RequestLogHandler h= new RequestLogHandler();
        File dir=new File(_logDir);
        //dir.mkdirs();
        String path= niceFPath(dir) + "/jetty-yyyy_mm_dd.log";
        
        tlog().debug("JettyIO: request-log output path {} ", path);
        
        NCSARequestLog requestLog = new NCSARequestLog(path) ;
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");

        h.setRequestLog(requestLog);        
        return h;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    @SuppressWarnings("unchecked")
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) throws Exception {
        props.put("filters", new ArrayList<Map<String,Object>>());
        props.put("urlpatterns", new ArrayList<String>());
        CmdLineQuestion q7= new CmdLineMandatory("fcz", getResourceStr(rcb, "cmd.http.fcz")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                List<Map<String,Object>> a= (List<Map<String,Object>>) props.get("filters");
                Map<String,Object> m= MP();
                String uri=nsb(props.remove("f_uri"));
                m.put("urlpattern", uri);
                m.put("class", answer);
                m.put("params", new HashMap<String,String>());
                a.add(m);
                return "fpath";
            }};
        CmdLineQuestion q6= new CmdLineMandatory("fpath", getResourceStr(rcb, "cmd.http.fpath")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                if (isEmpty(answer)) { props.remove("f_uri"); return ""; }
                props.put("f_uri", answer);
                return "fcz";
            }};
        CmdLineQuestion q5= new CmdLineQuestion("filters", getResourceStr(rcb,"cmd.http.filters"), "y/n","n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                return "Yy".indexOf(answer)>=0 ? "fpath" : "";
            }};
        CmdLineQuestion q4= new CmdLineMandatory("spath", getResourceStr(rcb,"cmd.http.spath")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                if (isEmpty(answer)) return "filters";
                List<String> lst= (List<String>) props.get("urlpatterns");
                lst.add(answer);
                return "spath";
            }};
        CmdLineQuestion q2= new CmdLineQuestion("base",getResourceStr(rcb,"cmd.http.resbase"), "",".") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("resbase", answer);
                return "spath";
            }};
        final CmdLineQuestion q1= new CmdLineQuestion("ctx", getResourceStr(rcb, "cmd.http.ctx")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("contextpath", answer);
                return "base";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1,q2,q4,q5,q6,q7){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }

}



