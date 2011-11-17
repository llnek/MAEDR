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

import com.zotoh.maedr.device.Event;

/**
 * When an event is spawned from a device, a job is created.  The runtime will decide on what processor
 * should handle this job, either by the "processor" property in the device configuration or via the application delegate.
 * 
 * @see com.zotoh.maedr.core.JobData
 *   
 * @author kenl
 */
public class Job { //implements java.io.Serializable {	
//    private static final long serialVersionUID = -6373728961805012065L;
    private Logger ilog() {  return _log=getLogger(Job.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private final AppEngine<?,?> _engine;
    private final long _jobID;
    private final JobData _data;
        
    /**
     * @param jobID
     * @param engine
     * @param event
     */
    public Job( long jobID, AppEngine<?,?> engine, Event event) {
    	this(jobID, engine, new JobData(event));
    }

    /**
     * @param jobID
     * @param engine
     * @param data
     */
    public Job( long jobID, AppEngine<?,?> engine, JobData data) {        
        tstObjArg("app-engine", engine);
        tstObjArg("job-data", data);
        _engine= engine;
        _data= data;        
        _jobID=jobID;
    }
    
    /**
     * @return
     */
    public AppEngine<?,?> getEngine() {               return _engine;           }
    
    /**
     * @param key
     * @param value
     */
    public void setData(Object key, Object value) {
        _data.setData(key, value);
    }
    
    /**
     * @param key
     * @return
     */
    public Object getData(Object key) {        return _data.getData(key);    }
    
    /**
     * @return
     */
    public Event getEvent() {        return _data.getEvent();    }
    
    /**
     * @return
     */
    public JobData getData() {                return _data;           }
    
    /**
     * @return
     */
    public long getID() {                return _jobID;           }

}
