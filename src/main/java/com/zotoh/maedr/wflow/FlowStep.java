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

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.Job;

import static com.zotoh.maedr.wflow.Reifier.*;

/**
 * @author kenl
 *
 */
public abstract class FlowStep  implements Runnable {

    private Logger ilog() {  return _log=getLogger(FlowStep.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
	
	private MiniWFlow _parent;
	private Activity _defn;
	private FlowStep _nextPtr;
	private String _core;
	private long _pid;
	private Object _closure;
	
	/**
	 * @param s
	 * @param a
	 */
	protected FlowStep(FlowStep s, Activity a) {
		_nextPtr=s;
		_defn=a;
		_parent= s.getFlow();
		_pid=_parent.nextAID();
	}
		
	/**
	 * @return
	 */
	public long getPID() { return _pid; }
	
	/**
	 * @param s
	 */
	protected FlowStep(MiniWFlow f) {
		_parent=f;
	}
	
	/**
	 * @param job
	 * @return
	 */
	public abstract FlowStep eval(Job job) throws Exception ;
	
	/**
	 * 
	 */
	public void realize() {
		getDefinition().realize(this);
		postRealize();
	}
	
	protected void postRealize() {}
	
	/**
	 * @return
	 */
	public FlowStep getNextStep() {
		return _nextPtr;
	}

	/**
	 * @return
	 */
	public Activity getDefinition() {
		return _defn;
	}

	/**
	 * @param c
	 */
	public void attachClosureArg(Object c) {
	    _closure=c;
	}

	/**
	 * @return
	 */
	public Object popClosureArg() {
        try { return _closure; } finally { _closure=null; }
    }

	/**
	 * 
	 */
	protected void clsClosure() { _closure=null; }
	
	/**
	 * @param core
	 */
	public void setCore(String core) { _core= nsb(core); }
	
	/**
	 * @return
	 */
	public String getCore() { return _core; }
	
	public MiniWFlow getFlow() { return _parent; }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
        FlowStep n= getNextStep();
		MiniWFlow f= getFlow();
		Job job= f.getJob();
		FlowStep rc=null;
		Activity err= null;
		
		try {
			rc=eval(job);
		}
		catch (Exception e) {
			err=f.onError(e);
		}
		
		if(err != null) {
			if (n==null) { n= reifyZero(f); }
			rc= err.reify(n);
		}
		
		if (rc==null) { 
			tlog().debug("FlowStep: rc==null => skip");
			// indicate skip, happens with joins
			return;
		}
		
        FlowRunner sc= f.getScheduler();
		String core = rc.getCore();
		
		if (rc instanceof NihilStep) {
			f.stop();
		}
		else if (rc instanceof AsyncWaitStep) {
			sc.hold(core, rc.getNextStep());
		}
		else if (rc instanceof DelayStep) {
			DelayStep ss= (DelayStep)rc;
			sc.delay(rc.getNextStep(), ss.getDelayMillis());
		}
		else {
			sc.run(core, rc);
		}
		
	}
	
}
