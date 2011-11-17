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
import com.zotoh.maedr.device.OneShotTimer;
import com.zotoh.maedr.device.TimerEvent;

/**
 * @author kenl
 *
 */
public class OnceTimerService extends TimerOne<OnceTimerService> {

    private TimerHandler _hdlr;
    
    /**
     * @param delaysecs
     * @return
     */
    public static OnceTimerService create(int delaysecs) {
        return new OnceTimerService(delaysecs);
    }
    
    /**
     * @return
     */
    public static OnceTimerService create() {       
        return new OnceTimerService(0);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> m) throws Exception {
        return new OneShotTimer(m);
    }

    private OnceTimerService(int secs) {
        super(secs);
    }
    
    @Override
    public ServiceCB<TimerEvent> getCB() {
        return new ServiceCB<TimerEvent>() {
            public void handleEvent(TimerEvent ev) {
                _hdlr.eval( ev);
            }
            public Class<TimerEvent> getEventType() {
                return TimerEvent.class;
            }            
        };
    }
    
}


