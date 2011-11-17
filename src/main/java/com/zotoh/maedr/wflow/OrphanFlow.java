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
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.netio.HTTPStatus;

/**
 * Deal with jobs which are not handled by any processor.
 * (Internal use only).  
 * 
 *
 * @author kenl
 */
public final class OrphanFlow extends MiniWFlow {
	
    
    /**
     * @param j
     */
    public OrphanFlow(Job j) {
        super(j);
    }

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.MiniWFlow#onStart()
	 */
	@Override
	protected Activity onStart() {
		final OrphanFlow me=this;
		return new PTask().withWork( new Work() {
			public void eval(Job job, Object closure) throws Exception {
	            Event ev= job.getEvent();
	            if (ev instanceof HttpEvent) {
	                me.handle( (HttpEvent) ev);
	            }
			}			
		});
	}

    private void handle(HttpEvent ev) throws Exception {
        HttpEventResult res= new HttpEventResult();
        res.setStatus(HTTPStatus.NOT_IMPLEMENTED);
//        res.setErrorMsg("Service not implemented");
        ev.setResult(res);
    }

	
}

