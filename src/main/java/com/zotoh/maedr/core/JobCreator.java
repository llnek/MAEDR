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
 
package com.zotoh.maedr.core;

import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.SeqNumGen;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.MemEvent;

/**
 * Creates jobs when an event is generated from a device.
 * 
 * @author kenl
 *
 */
public abstract class JobCreator<T,R> implements Vars {
    
    private Logger ilog() {  return _log=getLogger(JobCreator.class);    }    
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
//    private final AtomicLong _seed= new AtomicLong(0L);
    private final AppEngine<T,R> _engine;
                
    /**
     * @param engine
     */
    protected JobCreator(AppEngine<T,R> engine) {
        tstObjArg("app-engine", engine);        
        _engine=engine;
    }
    
    /**
     * @return
     */
    public AppEngine<T,R> getEngine() {        return _engine;    }    

    /**
     * @param event
     */
    public final void create(Event event)     {        
        Job job= new Job( nextJID(), _engine, event);
        Device v= event.getDevice();
        boolean sys= v.getId().matches(SYS_DEVID_REGEX);        
        onCreate(v, sys, job);    	
    }
    
    /**
     * @param v
     * @param sys
     * @param job
     */
    protected abstract void onCreate(Device v, boolean sys, Job job);
    
    /**
     * @return
     */
    public Job createMemJob() {
        Device dev= _engine.getDeviceManager().getDevice(INMEM_DEVID) ;
        return new Job( nextJID(), _engine,   new MemEvent(dev));        
    }
    
    private long nextJID()    {
        return SeqNumGen.getInstance().next() ;
    }


}
