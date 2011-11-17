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

import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zotoh.core.util.Logger;

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.*;

/**
 * A Thread pool.
 *
 * @author kenl
 */
class TCore implements java.io.Serializable, RejectedExecutionHandler  {
    
    private static final long serialVersionUID = 404521678153694367L;
    
    private Logger ilog() {  return _log=getLogger(TCore.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private ThreadPoolExecutor _scd;
    private final String _id;
        
    /**
     * @param name
     */
    public TCore(String name) {
        tstEStrArg("core-id", name) ;
        _id= nsb(name);
    }
    
    /**
     * @param tds
     */
    public void start( int tds) {
        activate( Math.max(1, tds) );
    }
    
    /**
     * 
     */
    public void start() {
        start(1);
    }
    
    /**
     * @param work
     */
    public void schedule(Runnable work) {    
//        if (tlog().isDebugEnabled())
//            tlog().debug("TCore: about to run work: " + work) ;
        _scd.execute(work);
    }
    
    /* (non-Javadoc)
     * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)    {
        //TODO: too much work for the core...
        tlog().warn("TCore: \"{}\" rejected work - threads/queue are max'ed out" , _id);
    }
    
    private void activate( int maxNumThreads) {        
        _scd = new ThreadPoolExecutor(maxNumThreads, maxNumThreads, 5000, 
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 
                new TFac( _id) , this );
        tlog().info("TCore: \"{}\" activated with threads = {}" , _id , maxNumThreads);                    
    }
        
}
