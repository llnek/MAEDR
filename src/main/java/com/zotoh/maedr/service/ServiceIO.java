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
 package com.zotoh.maedr.service;

import java.lang.reflect.Constructor;

import org.json.JSONObject;

import com.zotoh.core.util.GUID;
import com.zotoh.core.util.MetaUte;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.Event;
 
/**
 * @author kenl
 *
 */
public abstract class ServiceIO implements Vars {
    
    protected JSONObject _props= new JSONObject();
    private static Constructor<?> _grpCtor;
    
    static {
    	maybeGetGroupCtor();
    }
    
    /**
     * 
     */
    protected ServiceIO() {
        safePutProp("id", GUID.generate());
    }
    
    /**
     * @return
     */
    public String getId() {
    	return _props.optString("id");
    }
    
    /**
     * 
     */
    public void start() throws Exception {
    	ServiceGroup<?,?> g= (ServiceGroup<?,?>) _grpCtor.newInstance();    	
        g.add(this).start();
    }
    
    /**
     * @param m
     * @throws Exception
     */
    public void configDevice(DeviceManager<?,?> m) throws Exception {
        Device dev= newDevice(m);
        if (dev != null) {
            dev.configure(_props);
            m.add(dev);
        }
    }
    
    /**
     * @param m
     * @throws Exception
     */
    protected abstract Device newDevice(DeviceManager<?,?> m) throws Exception;
    
    /**
     * @return
     */
    public abstract ServiceCB<? extends Event> getCB();
    
    /**
     * @param name
     * @param obj
     */
    protected void safePutProp(String name, Object obj) {
        try {
            _props.put(name, obj);
        }
        catch (Exception e)
        {}
    }
    
    private static void maybeGetGroupCtor() {
    	try {
    		_grpCtor=MetaUte.loadClass("com.zotoh.maedr.wflow.FlowServiceGroup")
    		.getConstructor();
    	}
    	catch (Throwable t) {}
    	if (_grpCtor==null)
    	try {
    		_grpCtor=MetaUte.loadClass("com.zotoh.maedr.process.ProcServiceGroup")
    		.getConstructor();
    	}
    	catch (Throwable t) {}

    	if (_grpCtor==null) {
    		throw new RuntimeException("No ServiceGroup ctor found");
    	}
    }
    
    
    
}
