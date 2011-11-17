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

import com.zotoh.maedr.core.AppDelegate;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.JobCreator;
import com.zotoh.maedr.core.Scheduler;
import com.zotoh.maedr.device.Device;

/**
 * @author kenl
 *
 */
public class JobMaker extends JobCreator<Processor,Processor> {

    /**
     * @param engine
     */
    protected JobMaker(ProcBaseEngine engine) {
        super(engine);
    }

    @Override
    protected void onCreate(Device v, boolean sys, Job job) {
        AppDelegate<Processor,Processor> g= getEngine().getDelegate();
        Processor p=null;
        
        if (sys) {
            p= new BuiltinProcessor(job);
        } else {
            p= (Processor) v.getPipeline(job);
            if ( p==null) { p= g.newProcess(job); }                    
        }
        
        if (p==null) {
            p=handleOrphansAsProc(job) ;            
        }
        
        getEngine().getScheduler().run(Scheduler.EVENT_CORE, p);
        
    }

    private Processor handleOrphansAsProc(Job job) {        
        return new OrphanProcessor(job);
    }
    
}
