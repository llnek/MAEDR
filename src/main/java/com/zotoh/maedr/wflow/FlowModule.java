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

import java.util.Map;

import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.DelegateFactory;
import com.zotoh.maedr.core.Module;
import com.zotoh.maedr.service.ServiceIO;

/**
 * @author kenl
 *
 */
public class FlowModule extends Module<MiniWFlow, FlowStep> {

	/**
	 * @return
	 */
	public String getDefDelegateClass() {
		return "com.zotoh.maedr.wflow.DefaultDelegate";
	}
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.core.Module#getShortName()
	 */
	public String getShortName() { return "Flow"; }
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.core.Module#getName()
	 */
	public String getName() { return "Workflow"; }
	
    /**
     * 
     */
    public FlowModule()
    {}
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.Module#newEngine()
     */
    @Override
    public AppEngine<MiniWFlow, FlowStep> newEngine() {
        return new FlowBaseEngine();
    }

    @Override
    public DelegateFactory<MiniWFlow, FlowStep> newDelegateFactory(
            Map<String, ServiceIO> devs) {
        return new FlowDelegateFactory(devs);
    }

}
