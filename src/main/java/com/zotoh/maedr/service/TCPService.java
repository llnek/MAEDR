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

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.TCPEvent;
import com.zotoh.maedr.device.TcpIO;

/**
 * @author kenl
 *
 */
public class TCPService extends ServiceIO {

    private TCPHandler _hdlr;
    
    /**
     * @param port
     * @return
     */
    public static TCPService create(int port) {
        return new TCPService(port);
    }
    
    /**
     * @param host
     * @return
     */
    public TCPService host(String host) {
        safePutProp("host", host);
        return this;
    }

    /**
     * @param backlog
     * @return
     */
    public TCPService backlog(int backlog) {
        safePutProp("backlog", backlog);
        return this;
    }
    
    /**
     * @param h
     * @return
     */
    public TCPService handler(TCPHandler h) {
        _hdlr=h;
        return this;
    }
    
    /**
     * @param socTimeoutMillis
     * @return
     */
    public TCPService soctout(int socTimeoutMillis) {
        safePutProp("soctoutmillis", socTimeoutMillis);
        return this;
    }
    
    private TCPService(int port) {
        safePutProp("port", port);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> m) throws Exception {
        return new TcpIO(m);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#getCB()
     */
    @Override
    public ServiceCB<TCPEvent> getCB() {
        return new ServiceCB<TCPEvent>() {
            public void handleEvent(TCPEvent ev) {
                _hdlr.eval(ev);
            }
            public Class<TCPEvent> getEventType() {
                return TCPEvent.class;
            }            
        };
    }
    
}
