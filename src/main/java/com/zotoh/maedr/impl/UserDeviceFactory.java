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
 
package com.zotoh.maedr.impl;

import static com.zotoh.core.util.CoreUte.errBadArg;
import static com.zotoh.core.util.CoreUte.tstArgIsType;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.MetaUte.loadClass;
import static com.zotoh.core.util.StrUte.isEmpty;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.zotoh.maedr.core.DeviceFactory;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;

/**
 * The device factory for all built-in devices.
 *
 * @author kenl
 */
public class UserDeviceFactory<T,R> extends DeviceFactory<T,R> implements Vars {
    
	private Map<String,Constructor<?> > _devs= MP();
	
    /**
     * @param mgr
     */
    public UserDeviceFactory(DeviceManager<T,R> mgr) {
        super(mgr);
    }

    /**
     * @return
     */
    public Set<String> getUserDevs() {
    	return Collections.unmodifiableSet(_devs.keySet());
    }
    
    /**
     * @param type
     * @param deviceClass
     * @throws Exception
     */
    public void add(String type, String deviceClass) throws Exception {
    	
        if (isEmpty(type) || isEmpty(deviceClass)) 
        { return; }
        
        if (_devs.containsKey(type)) {
        	errBadArg("Device type: " + type + " is already defined.");
        }
        
        Class<?> z= loadClass(deviceClass) ;
        Constructor<?> ctor;
        
        tstArgIsType("device", z, Device.class) ;
        try {
            ctor= z.getConstructor(DeviceManager.class);
        }
        catch (Throwable t) {
            throw new InstantiationException("Class: " + deviceClass + " is missing ctor(DeviceManager)");                                                
        }
        
    	_devs.put(type, ctor);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.DeviceFactory#onNewDevice(com.zotoh.maedr.device.DeviceManager, java.lang.String, org.json.JSONObject)
     */
    protected Device onNewDevice(DeviceManager<T,R> dm, String type, JSONObject deviceProperties) throws Exception {
    	Device dev= null;
    	
    	if (  	_devs.containsKey(type)) {
    		dev= (Device) _devs.get(type).newInstance(dm);
    	}
    	
    	return dev;
    }

}
