/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.device;

import com.zotoh.core.util.Logger;


import static com.zotoh.core.util.LoggerFactory.getLogger;

import static com.zotoh.core.util.CoreUte.*;

/**
 * After a device generates an event, in most case, it has to wait for the downstream application code to
 * process this event, after which a result will be pass back to the device for replying back to the client.
 * 
 * The WaitEvent class is used by devices to put the event on hold until the result is back from the application.
 * 
 * @author kenl
 */
public abstract class WaitEvent {
    
    private transient Logger _log= getLogger(WaitEvent.class); 
    public Logger tlog() {         return _log;  }    
    private final Event _event;
    private EventResult _res;

    /**
     * @param res
     */
    public abstract void resumeOnEventResult(EventResult res) ;
        
    /**
     * @param millisecs
     */
    public abstract void timeoutMillis(long millisecs);
        
    /**
     * @param secs
     */
    public abstract void timeoutSecs(int secs);
        
    /**
     * @return
     */
    public Event getInnerEvent() {        return _event;    }
    
    /**
     * @return
     */
    public long getId()    {         return _event.getId();    }
    
    /**
     * Set the result directly.
     * 
     * @param obj the result.
     */
    public void setEventResult(EventResult obj)    {
        _res= obj;
    }
    
    /**
     * Get the result.
     * 
     * @return the result.
     */
    public EventResult getResult()    {        return _res;    }
        
    /**
     * @param ev
     */
    protected WaitEvent(Event ev) {
        tstObjArg("event-obj", ev) ;
        _event=ev;
        _event.bindWait(this) ;
    }
    
    
}
