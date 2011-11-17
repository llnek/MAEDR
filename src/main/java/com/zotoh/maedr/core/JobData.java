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

import static com.zotoh.core.util.LangUte.MP;

import java.util.Map;

import com.zotoh.maedr.device.Event;

/**
 * JobData is a transient collection of data belonging to a Job.  By default, it has a reference to the original event
 * which spawned the job.
 * If a Processor needs to persist some job data, those data should be encapsulate in a ProcessState object.
 * 
 * @see com.zotoh.maedr.process.ProcessState
 * @see com.zotoh.maedr.process.Processor
 * @see com.zotoh.maedr.core.Job
 *
 * @author kenl
 */
public class JobData implements java.io.Serializable {
    
    private static final long serialVersionUID = -6529216830940398433L;
    private Map<Object,Object> _data= MP();
    private transient Event _event;
    //private transient Stack<Object> _errors;
            
    /**
     * @param e
     */
    public void setEvent(Event e) {
        _event=e;
    }
    
    /**
     * @return
     */
    public Event getEvent() {        return _event;    }
    
    /**
     * @param key
     * @param value
     */
    public void setData( Object key, Object value) {        
        if ( key != null) { 
            _data.put(key, value) ;
        }        
    }
    
    /**
     * @param key
     * @return
     */
    public Object getData(Object key) {        
        return key==null ? null : _data.get(key);        
    }
    
    /**
     * @param key
     * @return
     */
    public Object removeData(Object key) {        
        return key==null ? null : _data.remove(key);        
    }
    
    /**
     * 
     */
    public void clearAll() {                _data.clear();         }

    /**
     * @param event
     */
    protected JobData(Event event) {  
        _event=event;       
    }
        
}
