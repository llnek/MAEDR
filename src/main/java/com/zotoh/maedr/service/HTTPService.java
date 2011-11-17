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

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.maedr.device.netty.NettpIO;
 
/**
 * @author kenl
 *
 */
public class HTTPService  extends BasicHTTP<HTTPService> {
    private HTTPHandler _hdlr;
    
    /**
     * @param port
     * @return
     */
    public static HTTPService create(int port) {
        return new HTTPService(port);
    }
    
    /**
     * @param port
     */
    protected HTTPService(int port) {
    	super(port);
    }
    
    /**
     * @param h
     * @return
     */
    public HTTPService handler( HTTPHandler h) {
        _hdlr=h; 
        return this;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> dm) throws Exception {
        return new NettpIO( dm);
    }
    
    /**
     * @return
     */
    public ServiceCB<HttpEvent> getCB() {
        return new ServiceCB<HttpEvent>() {
            public void handleEvent(HttpEvent ev) {
                _hdlr.eval( ev, new HttpEventResult());
            }
            public Class<HttpEvent> getEventType() {
                return HttpEvent.class;
            }            
        };
    }
    
    
    
    
    
    
}
