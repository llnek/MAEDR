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

import static com.zotoh.maedr.process.WorkUnit.NONE;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;

/**
 * Handles internal system events.
 * (Internal use only)
 *
 * @author kenl
 */
class BuiltinProcessor extends Stateless {

    /**
     * @param j
     */
    public BuiltinProcessor(Job j) {
        super(j);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#getNextWorkUnit()
     */
    @Override
    protected WorkUnit getNextWorkUnit() {
        int step= getCurStepPos();
        switch (step) {
            case 10001: return new ShutdownTask(this);
            case 10002: return new ShutdownTask(this);
        }
        return NONE;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.Processor#onStart(com.zotoh.maedr.device.Event)
     */
    @Override
    protected FlowInfo onStart(Event e) {
        String id= e.getDevice().getId();
        FlowInfo f=FlowInfo.END;
        
        if (SHUTDOWN_DEVID.equals(id)) {
            f= new FlowInfo(10001);
        }
        
        return f;
    }

}
