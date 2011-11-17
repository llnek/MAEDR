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
import com.zotoh.maedr.device.JmsEvent;
import com.zotoh.maedr.device.JmsIO;

/**
 * @author kenl
 *
 */
public class JMSMsgService extends ServiceIO {
    private JMSMsgHandler _hdlr;

    /**
     * @param contextFactory
     * @return
     */
    public static JMSMsgService create(String contextFactory) {
        return new JMSMsgService(contextFactory);
    }
    
    /**
     * @param cf
     * @return
     */
    public JMSMsgService connectionFactory(String cf) {
        safePutProp("connfactory", cf);
        return this;
    }
    
    /**
     * @param user
     * @return
     */
    public JMSMsgService jndiUser(String user) {
        safePutProp("jndiuser", user);
        return this;
    }
    
    /**
     * @param pwd
     * @return
     */
    public JMSMsgService jndiPwd(String pwd) {
        safePutProp("jndipwd", pwd);
        return this;
    }
    
    /**
     * @param user
     * @return
     */
    public JMSMsgService jmsUser(String user) {
        safePutProp("jmsuser", user);
        return this;
    }
    
    /**
     * @param pwd
     * @return
     */
    public JMSMsgService jmsPwd(String pwd) {
        safePutProp("jmspwd", pwd);
        return this;
    }
    
    /**
     * @param persist
     * @return
     */
    public JMSMsgService durable(boolean persist) {
        safePutProp("durable",persist);
        return this;
    }
    
    /**
     * @param url
     * @return
     */
    public JMSMsgService provideUrl(String url) {
        safePutProp("providerurl",url);
        return this;
    }
    
    /**
     * @param dest
     * @return
     */
    public JMSMsgService destination(String dest) {
        safePutProp("destination",dest);
        return this;
    }
    
    /**
     * @param h
     * @return
     */
    public JMSMsgService handler(JMSMsgHandler h) {
        _hdlr=h;
        return this;
    }
    
    private JMSMsgService(String ctx) {
        safePutProp("contextfactory", ctx);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> m) throws Exception {
        return new JmsIO(m);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#getCB()
     */
    @Override
    public ServiceCB<JmsEvent> getCB() {
        return new ServiceCB<JmsEvent>() {
            public void handleEvent(JmsEvent ev) {
                _hdlr.eval(ev);
            }
            public Class<JmsEvent> getEventType() {
                return JmsEvent.class;
            }            
        };
    }
    
    
    
    
}