/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
 
package com.zotoh.maedr.core;

import static com.zotoh.core.io.StreamData.setWDir;
import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.getCWD;
import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.CoreUte.tstArgIsType;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.MetaUte.loadClass;
import static com.zotoh.core.util.ProcessUte.safeThreadWait;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Properties;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.db.DBUte;
import com.zotoh.core.db.DBVendor;
import com.zotoh.core.db.DDLUte;
import com.zotoh.core.db.JDBC;
import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.db.JDBCPool;
import com.zotoh.core.db.JDBCPoolManager;
import com.zotoh.core.util.FileUte;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.device.DeviceManager;

/**
 * The AppEngine is the runtime which *hosts* the user application.  Essentially, a user application is a collection of
 * processors provided by the application to handle jobs, generated  by IO devices.  
 *
 * @author kenl
 */
public abstract class AppEngine<T,R> implements Vars {
    
    private Logger ilog() {  return _log=getLogger(AppEngine.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    

    private boolean _externalContainer=false,
    _active = false;
    
    private Properties _props= new Properties();
    private Object _block= new Object();    

    private JDBCPoolManager _poolMgr;
    private JDBCPool _pool;
    
    private DeviceManager<T,R> _devMgr;
    private File _appDir;
    private Config<T,R> _cfg;
    private File _pidFile;
    private Scheduler<T,R> _scheduler;
    private JobCreator<T,R> _jobCreator;
    private AppDelegate<T,R> _appMaster;


    /**
     * @return
     */
    public abstract Module<T,R> getModule();
    
    /**
     * @return the engine properties.
     */
    public Properties getProperties() {        return _props;    }
    
    
    /**
     * 
     */
    protected AppEngine() {}
    
        
    
    /**
     * @return
     */
    public File getAppDir() { return _appDir; }

    /**
     * @return
     */
    public DeviceManager<T,R> getDeviceManager() {        return _devMgr;    }
        
    /**
     * @return
     */
    public AppDelegate<T,R> getDelegate() {        return _appMaster;    }
    
    /**
     * @return
     */
    public JobCreator<T,R> getJobCreator() {        return _jobCreator;    }

    /**
     * @return
     */
    public Scheduler<T,R> getScheduler() {        return _scheduler;    }
    
    /**
     * @param props
     */
    public void load(Properties props) {
        _props.putAll(props);
    }
    
    /**
     * @param fac
     */
    public void bindDelegateFactory( DelegateFactory<T,R> fac) throws Exception {
        _appMaster= fac==null ? null : fac.create(this);
    }

    /**
     * @param s
     */
    public void bindScheduler(Scheduler<T,R> s) {
		if (s.getEngine() == this) {} else { throw new RuntimeException("Wrong Engine!"); }
		_scheduler=s; 
		_scheduler.iniz();
    }
    
    /**
     * @param cfg
     * @throws Exception
     */
    public void bind( IOConfigurator<T,R> cfg) throws Exception {
        if (cfg != null) {
            cfg.config( _devMgr);
        }
    }

    /**
     * @param props
     * @throws Exception
     */
    public void startViaServlet(File root, Properties props) throws Exception {
        tlog().info("AppEngine: startViaServlet() called");       
        _externalContainer=true;
        _appDir=root;
    	start(false, props);
    }
    
    /**
     * @throws Exception
     */
    public void startService() throws Exception {
        tlog().info("AppEngine: startService() called");       
        preBoot_0();
        boot();        
        blockAndWait();
    }
    
        
    /**
     * Starts the application.
     * @param props
     * @throws Exception
     */
    public void start( Properties props ) throws Exception {
        start(true, props);
    }

    private void start( boolean block, Properties props ) throws Exception {
        _props.putAll(props);
        start(block);
    }
    
    /**/
    public void shutdown() {        
        if (_active) {
            tlog().info("AppEngine: shutdown() called") ;
            stop();
            safeThreadWait(1500);
            synchronized(_block) {            
                try { _block.notify(); } catch (Exception e) {}
            }
        }
    }
    
    /**
     * @return
     */
    public JDBC newJdbc() {
        return _pool == null ? null : new JDBC(_pool);
    }
    
    /**
     * @param uri
     * @param pwd
     * @return
     */
    public boolean verifyShutdown(String uri, String pwd) {        
        tlog().debug("AppEngine: uri=> {} pwd=> {}", uri, pwd);
        return SHUTDOWN_URI.equals( nsb(uri)) ? verifyShutdown(pwd) : false;
    }
    
    /**
     * @return
     */
    public boolean isEmbedded() { return _externalContainer; }
    
    /**
     * @return
     */
    protected JDBCPool getDB() {        return _pool;           }
        
    private boolean verifyShutdown(String pwd) {
        String s= _props.getProperty(SHUTDOWN_PORT_PWD, "");
        try {
            s=nsb( PwdFactory.getInstance().create(s).getAsClearText());
        } 
        catch (Exception e) {
            tlog().warn("",e);
            s="";
        }
        return isEmpty(s) ? true : s.equals( pwd);
    }
    
    private void start(boolean block) throws Exception {
        tlog().info("AppEngine: start() called");       
        preBoot_0();
        preBoot_1();
        boot();        
        if (block) {        blockAndWait();  }
    }

    private void preBoot_0() throws Exception {    	

    	File cwd=null;
        String v;

        if (_appDir==null) {
            v= _props.getProperty(APP_DIR);
            cwd= isEmpty(v) ? getCWD() : new File(v);
        } else {
            cwd=_appDir;
        }
        
        // config system resources
        getScheduler().iniz();               
        
        v= trim( _props.getProperty(WORK_DIR));
        if (isEmpty(v)) {
        	v= niceFPath( new File(cwd, TMP));
        }
        setWDir(new File(v)) ;
        
        v= trim(_props.getProperty(FILE_ENCODING));
        if (!isEmpty(v)) {
            System.setProperty("file.encoding", v);
        }

    	System.getProperties().put(APP_DIR, niceFPath(cwd));
    	_props.put(APP_DIR, niceFPath(cwd));
        _appDir= cwd;
    }
        
    /**/
    @SuppressWarnings("unchecked")
	private void preBoot_1() throws Exception {                
        String str= trim( _props.getProperty(DELEGATE_CLASS));
        File cwd= getAppDir();
        Constructor<?> ctor=null;
        Class<?> z;
        
        // deal with delegate creation...
//        tstEStrArg("delegate-class", str);
        if (isEmpty(str)) {
            tlog().warn("AppEngine: no delegate class provided, the built-in delegate will be used");
            str= Module.getPipelineModule().getDefDelegateClass();
        }
        
        z= loadClass(str) ;
        tstArgIsType("Delegate-Class", z, AppDelegate.class);
        
        Constructor<?>[] cs= z.getConstructors();
        Class<?>[] zz;
        if (cs != null) for (int i=0; i < cs.length; ++i) {
            zz=cs[i].getParameterTypes();
            if (zz != null && zz.length > 0) {
                if ( AppEngine.class.isAssignableFrom( zz[0])) {
                    ctor= cs[i];
                    break;
                }
            }
        }
        
        if (ctor == null) {
            throw new InstantiationException("Class: " + str + " is missing ctor(AppEngine)");                        
        }
        _appMaster= (AppDelegate<T,R>) ctor.newInstance(this) ;
                
        // parse manifest file which has all the event sources defined...
        str= _props.getProperty(MANIFEST_FILE);
        if (isEmpty(str)) {
            str= niceFPath( new File( new File(cwd, CFG), APPCONF));
        }
//      tstEStrArg("conf-file-path", str);        
        tlog().debug("AppEngine: about to load conf file: {}" , str);
        _cfg.parse( new File( str).toURI().toURL() ) ;
    }
        
    /**/
    private void boot() throws Exception {            
    	
        tlog().debug("AppEngine: delegate class: {}" ,  _appMaster.getClass().getName() );        

		_cfg.onSys();
    	
        inizDB();
        loadDevices();
        
        
        if ( !isEmbedded())
        	try {  hookShutdown(); } 
        catch(Exception e) {}
    	
		maybeUpdateProcID();
    	
		_active=true;    	
        tlog().info("AppEngine: ready") ;
    }

    private void maybeUpdateProcID() throws IOException {
        String[] ss = nsb( ManagementFactory.getRuntimeMXBean().getName()).split("@");
        String pid= !isNilArray(ss) ? ss[0] : "???";
        tlog().info("AppEngine: process-id {}", pid);
        File cwd= getAppDir();
        if ( cwd.canWrite()) {      
            _pidFile =new File(cwd, PROCID); 
            writeFile(_pidFile, pid);
        }
    }
    
    private void hookShutdown() {
        tlog().debug("AppEngine: adding shutdown hook...") ;
        final AppEngine<T,R> me=this;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try { me.shutdown(); } catch (Throwable t) { tlog().warn("",t); }
            }
        });    	
    }
    
    private void blockAndWait()    {
    	
        synchronized(_block) {
            try  {
                _block.wait();
            }
            catch (Throwable t)             
            {}
        }        
        tlog().info("AppEngine: stopped");
    }    
    
    private void stop() {
        System.out.println("");
        
        FileUte.delete(_pidFile);
        finzDB();
        unloadDevices();
        
        try { _appMaster.onShutdown(); } catch (Exception e) {}
        _active=false;
    }
    
    private void unloadDevices() {
        tlog().debug("AppEngine: unloading event devices...");
        _devMgr.unload();        
    }
    
    private void loadDevices() throws Exception {
        tlog().info("AppEngine: loading devices...");
        _devMgr.load();
        tlog().info("AppEngine: devices loaded");
    }
    
    private void inizDB() throws SQLException, IOException {        
        boolean reset= "true".equalsIgnoreCase( trim(_props.getProperty(JDBC_RESET)));
        String url = trim(_props.getProperty(JDBC_URL));
        JDBCInfo p;
        
        if ( ! isEmpty(url)) {
            tlog().info("AppEngine: initializing state datastore");        
            p= new JDBCInfo(
                    trim(_props.getProperty(JDBC_DRIVER)),
                    url,
                    trim(_props.getProperty(JDBC_USER)),
                    trim(_props.getProperty(JDBC_PWD))) ;        
            maybeConfigDB(p, reset);            
            _poolMgr= new JDBCPoolManager();
            _pool= _poolMgr.createPool(p);
        }
    }
    
    /**/
    private void finzDB() {
        if (_poolMgr != null) {
            _poolMgr.finz();
        }
    }
    
    /**/
    private void maybeConfigDB(JDBCInfo p, boolean reset) throws SQLException, IOException {        
        if (reset || ! DBUte.tableExists(p, STATE_TABLE)) {
        	inizStateTables(p); 
    	}        
    }
    
    private void inizStateTables(JDBCInfo jp) throws SQLException, IOException    {
        DBVendor v= DBUte.getDBVendor(jp);
        if (DBVendor.NOIDEA == v) { throw new SQLException("Unknown DB: " + jp.getUrl()); }        
        String bd= "com/zotoh/maedr/db/" + v + ".sql" ;        
        String ddl= rc2Str(bd, "utf-8");
        if (isEmpty(ddl)) { throw new SQLException("Unsupported DB: " + v); }
        DDLUte.loadDDL(jp, ddl);                        
    }

    /**
     * @return
     */
    protected abstract JobCreator<T,R> inizJobCreator();
    
    /**
     * @return
     */
    protected abstract Scheduler<T,R> inizScheduler();
    
    /**
     * 
     */
    protected void iniz() {        
        
        _jobCreator= inizJobCreator();
        _scheduler = inizScheduler();
        
        // config depends on device-manager, so create it after dev-mgr
        _devMgr= new DeviceManager<T,R>(this); 
        _cfg= new Config<T,R>(this);
        
    }
    
    
}
