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

/**
 * The WFlowDelegate is the interface between the runtime and your application.  For example, when an event is spawned and a
 * job is created, the runtime maybe able to determine what processor should handle the job via device configurations.  However,
 * if that is not the case, the tuntime will call upon the delegate to decide on how to handle the job.
 * 
 * @see com.zotoh.maedr.wflow.DefaultDelegate
 * 
 * @author kenl
 */
public abstract class WFlowDelegate extends AppDelegate<MiniWFlow, FlowStep> {
    
    /**
     * @param eng
     */
    protected WFlowDelegate(FlowBaseEngine eng)    {
    	super( (AppEngine<MiniWFlow,FlowStep>) eng);
    }
    
}
