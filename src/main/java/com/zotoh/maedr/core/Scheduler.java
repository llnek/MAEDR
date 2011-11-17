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


import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.zotoh.core.util.Logger;

/**
 * The Scheduler manages a set of threadpools.  Applications can add more groups and pick and choose which processor
 * runs in which group.
 *
 * @author kenl
 */
public abstract class Scheduler<T,R> implements Vars {
    
    private Logger ilog() {  return _log=getLogger(Scheduler.class);    }    
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    public static final String EVENT_CORE= "#events#";
    public static final String WAIT_CORE= "#wait#";
    public static final String WORK_CORE= "#worker#";

    protected final Map<Long,Runnable> _holdQ= MP();
    protected final Map<Long,Runnable> _runQ= MP();
    protected final Map<Long,Long> _parQ= MP();    
    protected final Map<String,TCore> _cores= MP();
    
    private final AppEngine<T,R> _engine;
    private java.util.Timer _timer;

    
    /**
     * @param cz
     * @param job
     * @return
     */
    public abstract Pipeline newPipeline(String cz, Job job);
    
    /**
     * @param core
     * @param w
     */
    public void run(String core, Runnable w) {
        tstObjArg("runnable",w);
        core=nsb(core);
        if (isEmpty(core)) {            core=WORK_CORE;        }
        TCore c= _cores.get( nsb(core));
        if (c==null) {
            if (core.length() > 0) {
                tlog().warn("Scheduler: unknown core: {}", core);
            }
            c= _cores.get(WORK_CORE); 
        }
        preRun(w);
        c.schedule(w); 
    }
    
    /**
     * @param r
     */
    protected abstract void preRun(Runnable r);
    
    /**
     * @param pid
     * @param w
     */
    protected void onHold(long pid, Runnable w) {
        _runQ.remove(pid);
        _holdQ.put(pid, w) ;        
        tlog().debug("Scheduler: moved to pending wait, process: {}", w);
    }
    
    /**
     * @param core
     * @param pid
     * @param w
     */
    protected void onWake(String core, long pid, Runnable w) {
        _holdQ.remove(pid);
        _runQ.put(pid, w) ;
        run(core, w);
        tlog().debug("Scheduler: waking up process: {}", w) ;
    }
    
    /**
     * @param w
     */
    public void reschedule(Runnable w)    {
    	if (w == null) { return; }
        tlog().debug("Scheduler: restarting runnable: {}" , w);            
        run(WAIT_CORE, w);        	
    }
    
    /**
     * @return
     */
    public AppEngine<T,R> getEngine() {        return _engine;    }
    
    /**
     * @param w
     */
    public void run(Runnable w) {
        run(  WORK_CORE,  w);
    }
    
    /**
     * @param id
     * @param threads
     */
    public void addCore(String id, int threads) {        
        tstPosIntArg("threads", threads) ;
        tstEStrArg("core-id", id) ;        
        TCore  c= new TCore(id);
        _cores.put(id, c);
        c.start(threads);
    }
    
    /**
     * @param id
     */
    public void addCore(String id) {
        addCore(id,1);
    }
    
    /**
     * @param engine
     */
    protected Scheduler(AppEngine<T,R> engine) {
        _engine= engine;
    }
    
    /**/
    protected void iniz() {
    	
        Properties ps= _engine.getProperties();
        
        int e=asInt(trim(ps.getProperty(TDS_EVENTS)), 2);
        int w=asInt( trim(ps.getProperty(TDS_WORK)), 4);
        int t= asInt( trim(ps.getProperty(TDS_WAIT)), 2);
        
        _timer= new Timer("scheduler-timer", true); 
        _cores.clear();
        
        addCore(EVENT_CORE, e) ;
        addCore(WAIT_CORE, t) ;
        addCore(WORK_CORE, w) ;
    }
        
    /**/
    protected abstract class DelayedTask extends TimerTask {
        @Override  public void run()        {
            try { eval(); } catch (Exception e) {}
        }
        protected abstract void eval();
        protected DelayedTask()  {}
    }
    
    protected void addTimer(DelayedTask t, long delay) {
    	_timer.schedule(t, delay);
    }
    
}
