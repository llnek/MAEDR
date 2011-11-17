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

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.WState;
import com.zotoh.maedr.device.Event;

/**
 * A Stateless processor implies that jobs handled by this processor will not be persisted to database, as
 * no *state* needs to be shared across multiple jobs.
 *
 * @author kenl
 */
public abstract class Stateless extends Processor {

	/**
	 * @param j
	 */
	protected Stateless(Job j) {
		super(j);
	}
	
    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#reconcileState(com.zotoh.maedr.core.WState)
     */
    public void reconcileState(WState ps) throws Exception {    	
    	throw new Exception("Cannot reconcile state in a stateless processor");
    }
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.process.Processor#createState()
	 */
	@Override
	protected void createState() {
	}
	
    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#onEnd()
     */
    @Override
    protected void onEnd() {}
    

    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#onError(java.lang.Exception)
     */
    @Override
    protected FlowInfo onError(Exception e) {
        return FlowInfo.END;
    }
      
    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#onStart(com.zotoh.maedr.device.Event)
     */
    protected FlowInfo onStart(Event e) {
        return new FlowInfo(10001);
    }
    
}