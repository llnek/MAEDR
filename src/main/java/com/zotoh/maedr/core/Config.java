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


import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.zotoh.core.util.JSONUte;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.impl.DefaultDeviceFactory;
import com.zotoh.maedr.impl.UserDeviceFactory;

/**
 * Handles the reading and parsing of the device config file in JSON format.
 *
 * @author kenl
 */
public final class Config<T,R> implements Vars {
    
    private Logger ilog() {  return _log= getLogger(Config.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
	private final Map<String, DeviceFactory<T,R>> _devHandlers= MP();
    private final AppEngine<T,R> _engine;
        
    /**
     * @param engine
     */
    public Config( AppEngine<T,R> engine) {
        tstObjArg("app-engine", engine);
        _engine= engine;
        iniz();
    }

    /**
     * @return
     */
    public AppEngine<T,R> getEngine() { return _engine;}
    
    /**
     * @param cfgFile
     * @throws Exception
     */
    public void parse( URL cfgFile) throws Exception {
    	tstObjArg("config-file", cfgFile);    
        InputStream inp= cfgFile.openStream();
        try {
            JSONObject root=JSONUte.read(inp);
            if (root != null) {
            	onDevHdlers( root.optJSONObject(CFGKEY_DEVHDLRS) );
            	onDevices( root.optJSONObject(CFGKEY_DEVICES) );
            	onCores( root.optJSONObject(CFGKEY_CORES) );
            }
//            onSys();            	
        }
        finally {
            close(inp);
        }
    }
    
    /**
     * @throws Exception
     */
    protected void onSys() throws Exception {
        JSONObject obj= new JSONObject();
        String pval;
        
        // we add an internal event source that can generate in-memory events
        obj.put(CFGKEY_TYPE, DT_MEMORY) ;
        obj.put(DEV_STATUS, true) ;
        addDev(INMEM_DEVID, DT_MEMORY, obj);
        
        if ( !getEngine().isEmbedded()) {
            // check for shutdown port
            pval= trim( _engine.getProperties().getProperty(SHUTDOWN_PORT));        
            cfgShutdownHook(pval);
        }
        
    }
    
    /**/
    private void cfgShutdownHook(String portStr) throws Exception    {
        String ps="", host="";
    	JSONObject obj;
        int port= -1;        
        
        if ( ! isEmpty(portStr)) {
            int pos= portStr.lastIndexOf(':');
            ps=portStr;            
            if (pos > 0) {
                host= portStr.substring(0,pos);
                ps= portStr.substring(pos+1);
            }            
            port= asInt(ps,-1);
        }

        if (port > 0)        {
            if (isEmpty(host)) {            host="";        }
            obj=new JSONObject();
            obj.put(CFGKEY_TYPE, DT_HTTP);
            obj.put(DEV_STATUS, true);
            obj.put(CFGKEY_HOST, host);
            obj.put(CFGKEY_SOCTOUT, 5);
            obj.put(CFGKEY_PORT, port);
            obj.put("async", true);
            addDev(SHUTDOWN_DEVID, DT_HTTP, obj);
        }
        
    }
        
    private void onDevHdlers(JSONObject top) throws Exception    {
    	if (top ==null) {   	return ; }
    	
    	UserDeviceFactory<T,R> fac= new UserDeviceFactory<T,R>( _engine.getDeviceManager() );
    	String type;
    	
    	for ( Iterator<?> keys = top.keys(); keys.hasNext();) {
    		type=nsb(keys.next());
    		fac.add( trim(type), trim(top.optString(type)) );    		
            _devHandlers.put(type, fac) ;
    	}
    	
    }
    
    private void onCores(JSONObject obj) throws JSONException {
    	if (obj == null) {  return; }
    	String key;
    	for (Iterator<?> it= obj.keys(); it.hasNext(); ) {
    		key= (String) it.next();
    		onOneCore(key,  obj.getJSONObject(key) ) ;
    	}
    }
    
    private void onOneCore(String id, JSONObject obj) {
        int n= obj.optInt(CFGKEY_THDS);
        if (n > 0) { _engine.getScheduler().addCore(id, n) ; }
    }

    private void onDevices(JSONObject top) throws Exception {
    	if (top==null) { return; }
    	String key;
    	for (Iterator<?> it= top.keys(); it.hasNext(); ) {
    		key=(String) it.next();
    		onOneDevice(key, top.getJSONObject(key) ) ;
    	}
    }   

    private void onOneDevice( String id, JSONObject obj ) throws Exception {
    	String type = trim( obj.optString(CFGKEY_TYPE));
        tstEStrArg("device->type", type) ;
        addDev(id, type, obj);
    }

    private Device addDev(String id, String type, JSONObject obj) throws Exception    {        
        DeviceFactory<T,R> fac = _devHandlers.get(type) ;
        if (fac==null) {
            throw new InstantiationException("Config: no device-factory found for type: " + type);
        }
        Device dev=null;
        try {
            dev= fac.newDevice(id, obj);
        } 
        catch (Throwable t) {
            tlog().warn("",t);
        }
//        if (dev==null) {
//            throw new InstantiationException("Failed to create device type: " + type) ;
//        }        
        if (dev != null) {
            _engine.getDeviceManager().add(dev);
        }
        
        return dev;
    }
    
    private void iniz() {
        DefaultDeviceFactory<T,R> fac= new DefaultDeviceFactory<T,R>(_engine.getDeviceManager()) ;
        Set<String> types= DefaultDeviceFactory.getAllDefaultTypes();
        for (String s : types) { 
            _devHandlers.put(s, fac) ;
        }            
    }
    
    
}
