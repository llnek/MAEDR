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
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Job;

/**
 * The default implementation of an application delegate.  If a device configuration
 * has no processor defined (to handle events from that device), the event will be
 * ignored as the runtime has no knowledge of how to handle the event.
 * Therefore, if your application relies on the default delegate, all the
 * devices should have processor defined as part of their configurations.
 * 
 * @author kenl
 */
public class DefaultDelegate extends AppDelegate<MiniWFlow, FlowStep> {
    
    /**
     * @param eng
     */
    public DefaultDelegate(AppEngine<MiniWFlow,FlowStep> eng) {
        super(eng);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.AppDelegate#newProcess(com.zotoh.maedr.core.Job)
     */
    public MiniWFlow newProcess(Job job) {
        return MiniWFlow.FLOW_NUL;  
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.AppDelegate#onShutdown()
     */
    public void onShutdown() {}            
    
}

