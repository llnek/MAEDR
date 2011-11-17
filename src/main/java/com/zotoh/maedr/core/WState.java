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
 
package com.zotoh.maedr.core;

import static com.zotoh.core.util.LoggerFactory.getLogger;

import org.json.JSONObject;

import com.zotoh.core.util.Logger;

/**
 * Holds state information which may need to be made persistent in case of a Stateful processor.
 *
 * @author kenl
 */
public class WState {
    
	private Logger ilog() {  return _log=getLogger(WState.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }        
    
    private JSONObject _json= new JSONObject();
    private Pipeline _pipe;
    private Object _keyObj, 
    _trackObj;
    
    /**
     * @param tracker
     */
    public void setTracker(Object tracker) {
    	_trackObj=tracker;
    }
    
    /**
     * @return
     */
    public Object getTracker() {    	return _trackObj;    }
    
    /**
     * @return
     */
    public boolean hasKey() { return _keyObj != null; }
        
    /**
     * @param key
     */
    public void setKey(Object key) {
        _keyObj= key;
    }
        
    /**
     * @return
     */
    public Object getKey() {        return _keyObj;    }
        
    /**
     * @param p
     */
    protected WState(Pipeline p) {
        _pipe=p;
    }

    /**
     * @return
     */
    public JSONObject getRoot() { return _json; }
    
    /**
     * @param obj
     */
    protected void setRoot(JSONObject obj) {
        _json=obj;
    }
    
    /**
     * @return
     */
    public Pipeline getPipeline() { return _pipe; }
    
    
}
