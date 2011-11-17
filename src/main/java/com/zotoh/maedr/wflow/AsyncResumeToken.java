/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import com.zotoh.maedr.core.FAsyncResumeToken;

/**
 * @author kenl
 *
 */
public class AsyncResumeToken extends FAsyncResumeToken<FlowStep> {

    /**
     * @param p
     */
    public AsyncResumeToken(FlowStep p) {
        super(p);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.FAsyncResumeToken#resume(java.lang.Object)
     */
    public void resume(Object resultArg) {
        FlowStep p=(FlowStep)_proc;
        p=p.getNextStep();
        p.attachClosureArg(resultArg);
        p.getFlow().getEngine().getScheduler().reschedule(p);
    }
    
    
}
