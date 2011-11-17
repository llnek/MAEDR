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
 

package com.zotoh.maedr.device;

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.SeqNumGen;

/**
 * Base class for all other Events.
 *
 * @author kenl
 */
public abstract class Event implements java.io.Serializable {
    
    private static final long serialVersionUID = -1928500078786458743L;
//    private static AtomicLong _IDBAG= new AtomicLong();
    private Logger ilog() {       return _log=getLogger(Event.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {         return _log==null ? ilog() : _log;    }    
   
    private WaitEvent _waitEvent;
    private final Device _dev;
    private EventResult _res;    
    private final long _id;
    
    /**
     * By setting a result to this event, the device will resume processing on this event
     * and reply back to the client.
     * 
     * @param r
     */
    public final void setResult(EventResult r)    {
        _res= r;
        if (_waitEvent != null)
        try        {
            _waitEvent.resumeOnEventResult(_res) ;
        }
        finally {
            _dev.releaseEvent(_waitEvent) ;
            _waitEvent=null;
        }
    }
    
    /**
     * @return
     */
    public Device getDevice()    {        return _dev;    }
    
    /**
     * @return
     */
    public EventResult getResult() {        return _res;    }
    
    /**
     * @return
     */
    public long getId() {        return _id;    }
    
    /**
     * Override and do something special when this event is destroyed.
     */
    public void destroy() {  }
    
    /**
     * @param w
     */
    protected void bindWait(WaitEvent w) {        _waitEvent=w;    }
        
    /**
     * @param dev
     */
    protected Event(Device dev) {
        tstObjArg("device", dev) ;
        _dev= dev;
        _id= SeqNumGen.getInstance().next();
    }
    
}
