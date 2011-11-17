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

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import org.json.JSONObject;

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.core.util.Logger;

/**
 * The role of a DeviceFactory is to create devices of certain types.  If an application wants to introduce a new device type, the
 * application must also implement a device factory which can create those new type(s). 
 *
 * @author kenl
 */
public abstract class DeviceFactory<T,R> implements Vars {
	
    private Logger ilog() {  return _log=getLogger(DeviceFactory.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    private final DeviceManager<T,R> _devMgr;
        
    /**
     * @param id device name (id)
     * @param deviceProperties
     * @return
     * @throws Exception
     */
    public Device newDevice(String id, JSONObject deviceProperties) throws Exception {
        tstObjArg("device-properties", deviceProperties) ;
        String type= deviceProperties.optString("type");
        tstEStrArg("device-type", type) ;
        tstEStrArg("device-id", id) ;
        
        if (! deviceProperties.has(DEVID)) { 
            deviceProperties.put(DEVID, id);
        }
    	
    	Device dev= onNewDevice(getDeviceManager(), type, deviceProperties);
        if (dev != null) {
            dev.configure(  deviceProperties ) ;
        }
        return dev;
    }
        
    /**
     * @return
     */
    public DeviceManager<T,R> getDeviceManager() {
        return _devMgr;
    }
        
    protected abstract Device onNewDevice(DeviceManager<T,R> dm, String type, 
    				JSONObject deviceProperties) throws Exception;
    
    /**
     * @param mgr
     */
    protected DeviceFactory(DeviceManager<T,R> mgr) {
        _devMgr=mgr;
    }
    
}

