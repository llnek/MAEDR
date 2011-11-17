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

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.service.ServiceCB;

/**
 * Internal user only.
 * 
 * @author kenl
 *
 */
class AdhocFlow extends MiniWFlow {
    
    private final ServiceCB<? extends Event> _cb;
    
    /**
     * @param j
     * @param cb
     */
    public  AdhocFlow(Job j, ServiceCB<? extends Event> cb) {
        super(j);
        _cb=cb;
    }

    /**
     * @param job
     */
    public AdhocFlow(Job job) {
    	this (job, null);
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
	 * @see com.zotoh.maedr.wflow.MiniWFlow#onStart()
	 */
	@Override
	protected Activity onStart() {
		final AdhocFlow me=this;		
		return new PTask().withWork(new Work() {
			public void eval(Job job, Object closure) throws Exception {
                magicTrickToHandleGenericEvent( me._cb, job.getEvent());
                // this will fail!!!
//                _cb.handleEvent( _cb.getEventType().cast( job.getEvent()) ) ;				
			}});
	}

}
