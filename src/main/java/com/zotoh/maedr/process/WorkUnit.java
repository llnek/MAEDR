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
 
package com.zotoh.maedr.process;

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.Job;

/**
 * A unit of work.  A processor is made up of a collection of small work units working
 * in concert (like a mini-workflow).
 * 
 * @see com.zotoh.maedr.process.Processor
 * 
 * @author kenl
 */
public abstract class WorkUnit implements Task {
    
    private Logger ilog() {  return _log=getLogger(WorkUnit.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private final Processor _processor;
    private final String _id;
    
    /**
     * Use this to signify no work or task, in which case the runtime will end the processor.
     */
    public static final WorkUnit NONE= new WorkUnit() {
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            return FlowInfo.END;
        }        
    };
    
    /**
     * @param proc
     * @param id
     */
    public WorkUnit(Processor proc, String id) {
        tstObjArg("processor", proc) ;
        tstEStrArg("task-id", id) ;
        _processor=proc;
        _id= id;
    }
    
    /**
     * @param proc
     */
    public WorkUnit(Processor proc) {
    		this(proc,"no-name");
    }
    
    /**
     * @return
     */
    public Processor getProcessor() {        return _processor;           }
    
    /**
     * @param job
     * @param closureObject
     * @return
     */
    public FlowInfo eval(Job job, Object closureObject) {
        
//        tlog().debug("{}#{}#eval()", Thread.currentThread().getName() ,  this );    
        FlowInfo rc;        
        try {
            rc= evalOneStep(job, closureObject);
        }
        catch (Exception e) {
            rc= getProcessor().onError(e);
        }
        
        return rc;
    }
    
    /**
     * @return
     */
    public String getId() {        return _id;    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getProcessor().toString() + "#" + getId();
    }
            
    /**
     * @param job
     * @param closureObject
     * @return
     * @throws Exception
     */
    protected abstract FlowInfo evalOneStep(Job job, Object closureObject) throws Exception;
    
    private WorkUnit() { _processor=null; _id=""; }
    
}
