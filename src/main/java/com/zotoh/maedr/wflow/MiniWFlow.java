/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
 *
 * THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 * MODIFY IT UNDER THE TERMS OF THE APACHE LICENSimport static com.zotoh.core.util.LoggerFactory.getLogger;

E, 
 * VERSION 2.0 (THE "LICENSE").
 *
 * THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 * BUT WITHOUT ANY WAimport com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.DeviceFactory;
RRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
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

import static com.zotoh.maedr.wflow.Reifier.*;

import static com.zotoh.core.util.CoreUte.tstObjArg;

import java.util.Properties;

import com.zotoh.core.util.SeqNumGen;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Pipeline;



/**
 * @author kenl
 *
 */
public abstract class MiniWFlow extends Pipeline {

    public static final MiniWFlow FLOW_NUL= new MiniWFlow() {
        protected Activity onStart() {
            return new Nihil();
        }
    };
    
    private boolean _active;
	private long _pid;
	private Job _theJob;
	
    /**
     * @return
     */
    public Properties getEngineProperties() {
        return getJob().getEngine().getProperties();
    }
    
    /**
     * @return
     */
    public boolean isActive() { return _active; }
    
    /**
     * @return
     */
    public FlowBaseEngine getEngine() { return (FlowBaseEngine) getJob().getEngine();    }
    
    
	/**
	 * @param job
	 */
	protected MiniWFlow(Job job) {
        tstObjArg("job", job) ;
        _pid= nextPID();
        _theJob= job;
        tlog().debug("Workflow: {} => pid : {}" , getClass().getName() , _pid) ;
	}

	private MiniWFlow() {}
    

    /**
	 * @return
	 */
	protected long nextAID() { 
    	return SeqNumGen.getInstance().next();
	}
	
	/**
	 * @return
	 */
	public FlowRunner getScheduler() { 
		return (FlowRunner) ( _theJob==null ? null : _theJob.getEngine().getScheduler()); 
	}
	
	/**
	 * @return
	 */
	public long getPID() { return _pid; }
	
	/**
	 * 
	 */
	protected void onEnd() {}

	/**
	 * @param e
	 * @return
	 */
	protected Activity onError(Exception e) {
		tlog().error("", e);
		return null;
	}
	
	/**
	 * @return
	 */
	protected abstract Activity onStart();

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.core.Pipeline#getJob()
	 */
	public Job getJob() { return _theJob; }
	
	/**
	 * 
	 */
	protected void preStart() {}
	
	/**
	 * 
	 */
	public void start() {

        tlog().debug("Workflow: {} => pid : {} => starting" , getClass().getName() , _pid) ;
		
		FlowStep s, s1= reifyZero( this);
		Activity a1;
		
        preStart();		
		createState();
		
        a1= onStart();
		
		if (a1== null || a1 instanceof Nihil) {
			s= reifyZero(this);
		} else {
			s=a1.reify(s1);
		}
	
		try {
			getScheduler().run("", s);			
		}
		finally {
			_active=true;
		}
		
	}

	/**
	 * 
	 */
	public void stop() {
		try { 
			onEnd(); 
		} 
		catch (Throwable e) {
			tlog().error("",e);
		}
		
        tlog().debug("Workflow: {} => pid : {} => end" , getClass().getName() , _pid) ;
	}

	private long nextPID() { 
    	return SeqNumGen.getInstance().next();
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getSimpleName() + "(" + _pid + ")";
    }
	
}
