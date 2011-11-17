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

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.service.ServiceCB;

/**
 * @author kenl
 *
 */
public class AdhocProcessor  extends Stateless {
    
    private final ServiceCB<? extends Event> _cb;
    
    /**
     * @param j
     * @param cb
     */
    public  AdhocProcessor(Job j, ServiceCB<? extends Event> cb) {
        super(j);
        _cb=cb;
    }

    /**
     * @param job
     */
    public AdhocProcessor(Job job) {
    	super(job);
    	_cb=null;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#getNextWorkUnit()
     */
    @Override
    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new WorkUnit(this, "t1") {
                protected FlowInfo evalOneStep(Job job, Object closureObject)
                        throws Exception {            
                    magicTrickToHandleGenericEvent(_cb, job.getEvent());
                    // this will fail!!!
//                    _cb.handleEvent( _cb.getEventType().cast( job.getEvent()) ) ;
                    return FlowInfo.END;
                }                
            };
        }
        return WorkUnit.NONE;
    }
        
    /**
     * @param c
     * @param e
     */
    protected static <T extends Event> void magicTrickToHandleGenericEvent(ServiceCB<T> c, Event e ) {
        // this trick is needed to get this handlEvent working!!!
        c.handleEvent( c.getEventType().cast(e)  );
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#onStart(com.zotoh.maedr.device.Event)
     */
    @Override
    protected FlowInfo onStart(Event e) {
        return new FlowInfo(10001);
    }

}
