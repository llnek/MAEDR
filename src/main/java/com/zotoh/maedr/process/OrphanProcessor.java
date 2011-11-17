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
public final class OrphanProcessor extends Stateless {

    private class Task1 extends WorkUnit {
        public Task1(Processor proc) {
            super(proc, "task1");
        }
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            Event ev= job.getEvent();
            if (ev instanceof HttpEvent) {
                handle( (HttpEvent) ev);
            }
            return FlowInfo.END;
        }        
        private void handle(HttpEvent ev) throws Exception {
            HttpEventResult res= new HttpEventResult();
            res.setStatus(HTTPStatus.NOT_IMPLEMENTED);
//            res.setErrorMsg("Service not implemented");
            ev.setResult(res);
        }
    }
    
    /**
     * @param j
     */
    public OrphanProcessor(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        switch ( getCurStepPos()) {
            case 10101: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    @Override
    protected FlowInfo onStart(Event e) {
        return new FlowInfo(10101);
    }
    
    
    
}