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

/**
 * @author kenl
 *
 */
public class DelayStep extends FlowStep {

	private long _delayMillis;
	
	/**
	 * @param cur
	 * @param a
	 */
	protected DelayStep(FlowStep cur, Delay a) {
		super(cur,a);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	@Override
	public FlowStep eval(Job job) {		return this;	}


	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public long getDelayMillis() { return _delayMillis; }
	
	/**
	 * @param millis
	 * @return
	 */
	public DelayStep withDelay(long millis) {
		_delayMillis=millis;
		return this;
	}
	


}


