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

/**
 * The AppDelegate is the interface between the runtime and your application.  For example, when an event is spawned and a
 * job is created, the runtime maybe able to determine what processor should handle the job via device configurations.  However,
 * if that is not the case, the runtime will call upon the delegate to decide on how to handle the job.
 * 
 * @see com.zotoh.maedr.impl.DefautDelegate
 * 
 * @author kenl
 */
public abstract class AppDelegate<T,R> {
    
    private Logger ilog() {  return _log=getLogger(AppDelegate.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }        
    private final AppEngine<T,R> _engine;
    
    /**
     * Return a new processor which will own & work on this job.
     * 
     * @param job
     * @return
     */
    public abstract T newProcess( Job job);

    /**
     * @return
     */
    public AppEngine<T,R> getEngine() {        return _engine;    }
    
    /**
     * Perform any cleanup here.
     */
    protected void onShutdown() {}
    
    /**
     * @param eng
     */
    protected AppDelegate(AppEngine<T,R> eng)    {
        tstObjArg("app-engine", eng) ;
        _engine=eng;
    }
    
}
