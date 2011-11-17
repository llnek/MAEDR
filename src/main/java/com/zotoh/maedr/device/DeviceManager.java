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
 
package  com.zotoh.maedr.device;


import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Vars;

/**
 * Manages all devices.
 * 
 * @author kenl
 */
public class DeviceManager<T,R> implements Vars {
    
    private Logger ilog() {  return _log=getLogger(DeviceManager.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private final Map<String,Device> _devices= MP();
    private AppEngine<T,R> _engine;
    
    
    /**
     * @param engine
     */
    public DeviceManager(AppEngine<T,R> engine) {
        tstObjArg("app-engine", engine) ;
        _engine=engine;
    }
    
    /**
     * @return
     */
    public AppEngine<T,R> getEngine() {        return _engine;    }

    /**
     * @return
     */
    public Collection<Device> listDevices() {
    	return Collections.unmodifiableCollection( _devices.values());
    }
    
    /**
     * @throws Exception
     */
    public final void load() throws Exception {
        for (Device v : _devices.values()) {
            v.start(); 
        }
    }
        
    /**
     * 
     */
    public final void unload() {        
        for (Device v : _devices.values()) {
            v.stop(); 
        }
    }
        
    /**
     * @param device
     * @throws Exception
     */
    public void add(Device device) throws Exception {        
        tstObjArg("device", device) ;        
        String id= device.getId();        
        if (hasDevice(id)) {
            throw new Exception("Device: \"" + id + "\" already exists");
        }        
        _devices.put(id, device);
    }
    
    /**
     * @param id
     * @return
     */
    public boolean hasDevice(String id) {
        return id==null ? false : _devices.containsKey(id);
    }
    
    /**
     * @param id
     * @return
     */
    public Device getDevice(String id) {
        return id==null ? null : _devices.get(id);        
    }
    
    
}
