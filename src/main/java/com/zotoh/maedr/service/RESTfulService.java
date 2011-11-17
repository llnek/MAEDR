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
 package com.zotoh.maedr.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.RESTEvent;
import com.zotoh.maedr.device.RESTEventResult;
import com.zotoh.maedr.device.netty.RestIO;
 
/**
 * @author kenl
 *
 */
public class RESTfulService  extends BasicHTTP<RESTfulService> {
    private RESTfulHandler _hdlr;
    
    /**
     * @param port
     * @return
     */
    public static RESTfulService create(int port) {
        return new RESTfulService(port);
    }
    
    /**
     * @param port
     */
    protected RESTfulService(int port) {
        super(port);
        safePutProp("resources", new JSONArray());
    }
    
    /**
     * @param context
     * @return
     */
    public RESTfulService contextPath(String context) {
    	safePutProp("contextpath", context);
    	return this;
    }
    
    /**
     * @param path
     * @return
     */
    public RESTfulService resourceMap(String path) {
    	JSONObject obj= new JSONObject();
    	try {
//    		obj.put("processor", "com.zotoh.maedr.service.AdhocProcessor");
    		obj.put("path", path);
    		_props.optJSONArray("resources").put(obj) ;
    	}
    	catch (JSONException e) 
    	{}
    	return this;
    }
    
    /**
     * @param h
     * @return
     */
    public RESTfulService handler( RESTfulHandler h) {
        _hdlr=h; 
        return this;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> dm) throws Exception {
        return new RestIO(dm);
    }
    
    /**
     * @return
     */
    public ServiceCB<RESTEvent> getCB() {
        return new ServiceCB<RESTEvent>() {
            public void handleEvent(RESTEvent ev) {
                _hdlr.eval( ev, new RESTEventResult());
            }
            public Class<RESTEvent> getEventType() {
                return RESTEvent.class;
            }            
        };
    }
    
    
    
    
    
    
}
