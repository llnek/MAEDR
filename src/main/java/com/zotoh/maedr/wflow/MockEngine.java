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

import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.JobCreator;
import com.zotoh.maedr.core.Module;
import com.zotoh.maedr.core.Scheduler;

/**
 * @author kenl
 *
 */
public class MockEngine extends AppEngine<MiniWFlow,FlowStep> {

	/**
	 * 
	 */
	public MockEngine()
	{}

	@Override
	public Module<MiniWFlow, FlowStep> getModule() {
		return null;
	}

	@Override
	protected JobCreator<MiniWFlow, FlowStep> inizJobCreator() {
		return null;
	}

	@Override
	protected Scheduler<MiniWFlow, FlowStep> inizScheduler() {
		return null;
	}
	
}
