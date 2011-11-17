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
import static com.zotoh.core.util.StrUte.nsb;

import com.zotoh.core.util.MetaUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Pipeline;
import com.zotoh.maedr.core.Scheduler;

/**
 * @author kenl
 *
 */
public class ProcRunner extends Scheduler<Processor, Processor> {

    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.Scheduler#newPipeline(java.lang.String, com.zotoh.maedr.core.Job)
     */
    public Pipeline newPipeline(String cz, Job job) {
    	Processor rc= null;
    	try {
    		rc= (Processor) MetaUte.loadClass(cz).getConstructor(Job.class).newInstance(job) ;
    	}
    	catch (Exception e) {
    		tlog().warn("",e);
    	}
    	return rc;
    }
	
    /**
     * @param w
     */
    protected void preRun(Runnable w) {
        if (w instanceof Processor) {
            long pid = ((Processor) w).getPID();
            _holdQ.remove(pid);
            _runQ.put(pid, w) ;
        }
    }
    
    /**
     * Put this processor to sleep for some time, then resume it.
     * 
     * @param flow
     * @param w
     */
    public void delay(FlowInfo flow, Processor w)     {
    	tstObjArg("flow-info", flow);
    	tstObjArg("processor", w);
        Object cobj= flow.getClosureArg();
        String n= nsb( flow.getNextQueue());
        long millis= flow.getDelayMillis();
        
        w.attachClosureArg(cobj);
                
        tlog().debug("Scheduler: delaying eval on core: {} for processor: {}, wait-millis: {}, closure= {}" , 
                n, w, millis, cobj);
                
        // implement callback
        final Processor pp= w;
        final String cc= n;        
        final ProcRunner _s= this;
                
        addTimer(new DelayedTask(){
            protected void eval() { _s.wakeup(cc,pp); }
            },  
            millis);        
    }
    
    /**
     * Look for the parent processor, if the parent is currently on-hold.
     * 
     * @param childPID
     * @return
     */
    public Processor getParent(long childPID) {
        Long ppid=_parQ.get(childPID);
        return ppid==null ? null : (Processor) _holdQ.get( ppid) ;
    }
    
    /**
     * Shutdown this processor, and if it has a parent, resume the parent.
     * 
     * @param w
     */
    public void shutdown(Processor w) {        
    	if (w==null) { return; }
        tlog().debug("Scheduler: killing processor: {}", w);        
        Long ppid, pid= w.getPID();
        _holdQ.remove(pid);
        _runQ.remove(pid);        
        ppid= _parQ.get(pid);
        if (ppid != null) {
        	// maybe restart parent if parent was blocked
        	reschedule( _holdQ.remove(ppid) );
        }
    }
    
    
    /**
     * @param par
     * @param child
     */
    public void waitChild(Processor par, Processor child) {
        tstObjArg("parent-processor", par);
        tstObjArg("child-processor", child);
        long cid= child.getPID(),
        pid= par.getPID();
        
        _runQ.put(cid, child);
        _runQ.remove(pid);
        
        _holdQ.put(pid, par) ;
        _holdQ.remove(cid) ;
        
        _parQ.put(cid, pid) ;

        tlog().debug("Scheduler: about to block-wait a child process: {} , parent process: {}" , child, par);        
    }
    
    /**
     * @param core
     * @param w
     */
    public void hold(String core, Processor w) {
        tstObjArg("processor", w);        
        onHold(w.getPID(), w);
    }
    
	
	/**
	 * @param eng
	 */
	public ProcRunner(ProcBaseEngine eng) {
		super(eng);
	}
	
	
    /**/
    private void wakeup(String core, Processor w) {
    	onWake(core, w.getPID(), w);
    }
    
	
	
}
