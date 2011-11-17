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
import com.zotoh.maedr.device.POP3Event;
import com.zotoh.maedr.device.PopIO;
 
/**
 * @author kenl
 *
 */
public class POP3Service  extends TimerMulti<POP3Service> {
    private POP3Handler _hdlr;
    
    /**
     * @param port
     * @return
     */
    public static POP3Service create(String host) {
        return new POP3Service(host, 110);
    }
    
    /**
     * @param host
     * @param port
     */
    protected POP3Service(String host, int pt) {
        super(60);
        safePutProp("host", host);
        port(pt);
    }
    
    /**
     * @param port
     * @return
     */
    public POP3Service port(int port) {
        safePutProp("port", port);
        return this;
    }
    
    /**
     * @param pwd
     * @return
     */
    public POP3Service password(String pwd) {
        safePutProp("pwd", pwd);
        return this;
    }
    
    /**
     * @param user
     * @return
     */
    public POP3Service user(String user) {
        safePutProp("user", user);
        return this;
    }
    
    /**
     * @param ssl
     * @return
     */
    public POP3Service ssl(boolean ssl) {
        safePutProp("ssl", ssl);
        return this;
    }
    
    /**
     * @param del
     * @return
     */
    public POP3Service deletemsg(boolean del) {
        safePutProp("deletemsg", del);
        return this;
    }
    
    /**
     * @param h
     * @return
     */
    public POP3Service handler( POP3Handler h) {
        _hdlr=h; 
        return this;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> dm) throws Exception {
        return new PopIO(dm);
    }
    
    /**
     * @return
     */
    public ServiceCB<POP3Event> getCB() {
        return new ServiceCB<POP3Event>() {
            public void handleEvent(POP3Event ev) {
                _hdlr.eval( ev);
            }
            public Class<POP3Event> getEventType() {
                return POP3Event.class;
            }            
        };
    }
    
    
    
    
    
    
}
