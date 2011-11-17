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
 
package com.zotoh.maedr.wflow;

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
public class FlowRunner extends Scheduler<MiniWFlow, FlowStep> {

    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.Scheduler#newPipeline(java.lang.String, com.zotoh.maedr.core.Job)
     */
    public Pipeline newPipeline(String cz, Job job) {
    	MiniWFlow rc= null;
    	try {
    		rc= (MiniWFlow) MetaUte.loadClass(cz).getConstructor(Job.class).newInstance(job) ;
    	}
    	catch (Exception e) {
    		tlog().warn("",e);
    	}
    	return rc;
    }
	
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.Scheduler#preRun(java.lang.Runnable)
     */
    protected void preRun(Runnable w) {
        if (w instanceof FlowStep) {
            long pid = ((FlowStep) w).getPID();
            _holdQ.remove(pid);
            _runQ.put(pid, w) ;
        }
    }
    
    /**
     * @param core
     * @param w
     */
    public void hold(String core, FlowStep w) {
        tstObjArg("workflow", w);
        onHold(w.getPID(), w);
    }
    
    /**/
    private void wakeup(String core, FlowStep w) {
    	onWake(core, w.getPID(), w);
    }
        
    /**
     * @param w
     * @param delayMillis
     */
    public void delay(FlowStep w, long delayMillis)     {
    	tstObjArg("flow-info", w);
    	
        // implement callback
        final String cc= nsb( w.getCore());        
        final FlowStep pp= w;
        final FlowRunner _s= this;
        
    	if (delayMillis < 0L) { hold(cc, w); return; }
    	if (delayMillis == 0L) { run(cc, w); return; }
        
        addTimer(new DelayedTask(){
            	protected void eval() { _s.wakeup(cc,pp); }
            }, delayMillis);
        
        tlog().debug("Scheduler: delaying eval on core: {} for workflow: {}, wait-millis: {}" , 
                        cc, w, delayMillis);                        
    }
    
	
	/**
	 * @param eng
	 */
	public FlowRunner(FlowBaseEngine eng) {
		super(eng);
	}

}
