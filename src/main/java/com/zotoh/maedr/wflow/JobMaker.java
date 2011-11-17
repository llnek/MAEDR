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

import com.zotoh.maedr.core.AppDelegate;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.JobCreator;
import com.zotoh.maedr.device.Device;

/**
 * @author kenl
 *
 */
public class JobMaker extends JobCreator<MiniWFlow, FlowStep> {

    /**
     * @param engine
     */
    public JobMaker(FlowBaseEngine engine) {
        super(engine);
    }

    private boolean isNil(MiniWFlow f) {
        return f==null || f == MiniWFlow.FLOW_NUL ;
    }
    
    protected void onCreate(Device v, boolean sys, Job job)     {        
        AppDelegate<MiniWFlow, FlowStep> g= getEngine().getDelegate();
        MiniWFlow f;
        
        if (sys) {
            f= new BuiltinFlow(job);
        }
        else {
            f= (MiniWFlow) v.getPipeline(job);
            if ( isNil(f)) { f= g.newProcess(job); }                    
        }
        
        if (isNil(f)) {
            f=handleOrphansAsFlow(job) ;            
        }
        
        if (!isNil(f)) {
            f.start();
        }
        
    }
    
    /**/
    private MiniWFlow handleOrphansAsFlow(Job job) {        
        return new OrphanFlow(job);
    }
    
    
}
