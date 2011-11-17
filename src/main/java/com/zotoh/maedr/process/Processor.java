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
 
package com.zotoh.maedr.process;

import static com.zotoh.core.util.CoreUte.tstObjArg;

import java.util.Properties;

import com.zotoh.core.util.SeqNumGen;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Pipeline;
import com.zotoh.maedr.device.Event;

/**
 * A Processor is made up of a collection of discrete tasks or workunits (like a mini-workflow).  
 * When a job is given to a processor,
 * the set of steps needed to *process* the job is controlled by the flow amongst these work units.  
 * When a workunit is done, the next workunit will get scheduled by the runtime engine.  
 * This allows for a more evenly distributed sharing of CPU/threads when handling a large amount of events/jobs, 
 * and encourage more asynchronous style of programming.
 *
 * @author kenl
 * 
 */
public abstract class Processor extends Pipeline implements Runnable {
    
    public static final int START = Integer.MIN_VALUE + 1;
    public static final int END = Integer.MIN_VALUE;
    public static final int NOOP = Integer.MIN_VALUE+2;
    
    public static final int FORK_WAIT = Integer.MIN_VALUE+30;
    public static final int FORK_SPLIT = Integer.MIN_VALUE+31;
    
    private int _curStep=START;
    private Job _curJob;
    private boolean _active= false;
    private Object _closureArg;
    private long _pid;
    private Exception _lastError;
    
    /*
     * Application SHOULD only use step values greater than the USER_BASE_MARKER.
     */
    public static final int USER_BASE_MARKER = 10000;
    
    /**
     * @param closure
     */
    public void attachClosureArg(Object closure) {
        _closureArg=closure;
    }
    
    /**
     * @return
     */
    public Object getClosureArg()    {        return _closureArg;    }
        
    /**
     * @return
     */
    public long getPID() {        return _pid;    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        eval();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getSimpleName() + "(" + _pid + ")";
    }
    
    /**
     * @return
     */
    public Properties getEngineProperties() {
        return getJob().getEngine().getProperties();
    }
    
    /**
     * @return
     */
    public AppEngine<?,?> getEngine() {        return getJob().getEngine();    }
    
    /**
     * @return
     */
    public int getCurStepPos() {        return _curStep;    }
    
    /**
     * @return
     */
    public FlowInfo moveToEnd() {        return FlowInfo.END;    }
        
    /**
     * @param child
     */
    protected void setDataLink(Processor child) {                
        if (child != null) {
            Properties p= new Properties();
            onSetDataLink(child, p) ;        
            child.onReceiveDataLink(p);            
        }
    }
    
    /**
	 * As parent, if there's some input for the child,
	 * put the input into p - the uplink.
     * This method will be called in the context of a parent process.
	 * 
     * @param child
     * @param p
     */
    protected void onSetDataLink(Processor child, Properties p)    {
    	// for example.
    	// p.put("p1", "hello");
    	// if (child instanceof abc) { p.put("p2", "aaa"); }
    }
    
    /**
     * As child, receive input from parent
     * before start.
     * This method will be called in the context of a child process.
     * 
     * @param p
     */
    protected void onReceiveDataLink(Properties p)    {
    	// for example.
    	// p.get("p1");
    	// p.get("p2");
    }
    
    /**
     * As parent, maybe receives data back from child
     * upon termination of child.
     * This method will be called in the context of a parent process.
     * 
     * @param child
     * @param p
     */
    protected void onChildGiveback(Processor child, Properties p)    {
    	// for example.
    	// p.get("output", "hello world");
    }
    
    /**
     * Maybe give some data back to parent, p is the uplink.
     * This method will be called in the context of a child process.
     * 
     * @param p
     */
    protected void onFinalGiveback(Properties p)    {
    	// for example.
    	// p.put("result", 23);
    }
    
    /**
     * @param j
     */
    protected Processor(Job j) {
        tstObjArg("job", j) ;
        _pid= nextPID();
        _curJob= j;
        tlog().debug("Processor: {} => pid : {}" , getClass().getName() , _pid) ;
    }
    
    /**
     * @return
     */
    public Job getJob() {        return _curJob;    }
        
    /**
     * @return
     */
    protected abstract WorkUnit getNextWorkUnit();
        
    /**
     * The processor is about to be started by the scheduler, if the application needs to do some initializing..etc.
     * do it here.
     * 
     * @param e the event that triggered the start of this processor.
     * @return the first step.
     */
    protected abstract FlowInfo onStart(Event e);
    
    /**
     * Let the application do something special when the processor ends.
     */
    protected abstract void onEnd() ;
    
    /**
     * If one of the work unit throws an exception, the runtime will give the processor a chance to
     * handle the error.  It is up to the application to either end the processor, by returning
     * FlowInfo.END or something else, if the logic takes a different path.
     *  
     * @param e
     * @return
     */
    protected abstract FlowInfo onError(Exception e);

    /**
     * 
     */
    protected void resetLastError() {
        _lastError=null;
    }
    
    /**
     * @param e
     */
    protected void pushError(Exception e) {
        _lastError=e;
    }
    
    /**
     * @return
     */
    protected Exception peekError() {        return _lastError;    }
    
    /**
     * @return
     */
    protected Exception popError() {
        Exception e= _lastError;
        _lastError=null;
        return e;
    }
    
    /**
     * @param next
     */
    protected void moveStepTo(int next) {
      _curStep= next;  
    }
    
    /**/
    private void eval() {        
        ProcRunner sch= (ProcRunner) getJob().getEngine().getScheduler();
        WorkUnit work= null;
        Exception error= null;
        String core;        
        FlowInfo flow= null;
        
        try {

            switch ( _curStep ) {
                
                case START: 
                    flow = begin(getJob().getEvent() ); 
                break;
                
                case END: 
                    try { onEnd(); } finally { onFinal(); }
                break;
                
                default:
                    
                    if (isActive()) {
                        work= getNextWorkUnit();                        
                    }
                    
                    if (WorkUnit.NONE.equals(work) || work==null) {
                    } else { 
                    	flow= work.eval( _curJob, _closureArg); 
                	}                                
                    
                break;
            }
            
        }
        catch (Exception e) {
            error=e;  
            tlog().error("",e);                        
        }

        if ( !isActive()) {
            return;
        }
        
        flow= preprocessForNextRun(flow, error);
        core= flow.getNextQueue();
        
        // check if we are spawning a child process, handling async...
        if ( ! maybeSpecialCase(flow, sch)) {
            schedule(flow, core, sch);
        }
        
    }

    /**/
    private FlowInfo preprocessForNextRun(FlowInfo flow, Exception error) {
        
        if (error != null) { 
            flow = onTaskError( error); 
        }
        
        if (flow==null) {   
            flow= moveToEnd();     
        }

        _closureArg = flow.getClosureArg();
        _curStep= flow.getNextStep();
        
        return flow;
    }
    
    /**/
    private void schedule(FlowInfo flow, String core, ProcRunner sch) {
        if ( flow.getDelayMillis() > 0L) {
            sch.delay( flow, this);                             
        } else {
            sch.run( core, this);
        }                    
    }
    
    /**/
    private boolean maybeSpecialCase(FlowInfo f, ProcRunner sch)    {

        try {
            
            if (f instanceof FAsyncWaitInfo) {          
                // user is calling some (unmanaged) async code, will call back later
                // so we put this processor into *wait state*
                sch.hold("", this);
                return true;
            }
            
            if (f instanceof FWaitInfo) {
                forkWait( (FWaitInfo)f, sch );
                return true;
            }
            
            if (f instanceof FSplitInfo) {
                forkSplit( (FSplitInfo)f, sch );
            }
            
            return false;
        }
        catch (Exception e) {
            f= preprocessForNextRun(f, e);
            schedule(f, f.getNextQueue(), sch);
        }
        
        return true;        
    }
    
    /**/
    private void forkSplit(FSplitInfo f, ProcRunner sch) throws Exception  {
        Class<?> z= (Class<?>) f.getClosureArg();
        tstObjArg("Child process class", z);
        boolean shared= f.isJobShared();
        Processor child= null;
        Job j= shared ? getJob() : getEngine().getJobCreator().createMemJob();
        
        tlog().debug("Processor: about to spawn a child process: {}" , z.getName());
        child = (Processor) z.getConstructor(Job.class).newInstance(j);
        sch.run(child) ;
    }
    
    /**/
    private void forkWait(FWaitInfo f, ProcRunner sch) throws Exception   {
        Class<?> z= (Class<?>) f.getClosureArg();
        tstObjArg("Child process class", z);
        boolean shared= f.isJobShared();
        Processor child= null;
        Job j= shared ? getJob() : getEngine().getJobCreator().createMemJob();
        
        tlog().debug("Processor: about to spawn a child process: {}" , z.getName());
        child = (Processor) z.getConstructor(Job.class).newInstance(j);        
        setDataLink(child);
        sch.waitChild(this, child) ;
        sch.run(child) ;
    }
    
    /**/
    public boolean isActive() { return _active; }
    
    /**/
    private FlowInfo onTaskError(Exception e) {
        _lastError=e;
        return onError(e) ;
    }
    
    /**/
    private FlowInfo begin(Event ev) {
        
        try {
            createState();
            _active=true;
        }
        catch (Exception e) {
            return onError(e);
        }
        
        tlog().debug("Processor: about to call onStart() => {}" , toString());
        
        return onStart(ev);        
    }

    /**/
    private long nextPID() {
    	return SeqNumGen.getInstance().next();
    }
    
    /**/
    private void onFinal() {
        ProcRunner sch= (ProcRunner) getEngine().getScheduler();
        Properties props= new Properties();
        Processor par= sch.getParent(getPID()) ;

        onFinalGiveback(props);
        if (par != null) {
            par.onChildGiveback(this, props) ;
        }

        _active=false;
        
        sch.shutdown(this);
    }
    
    
    
}
