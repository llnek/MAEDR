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

import static com.zotoh.maedr.wflow.Reifier.*;

/**
 * @author kenl
 *
 */
public class Delay extends Activity {

	private long _delayMillis;
	
	/**
	 * @param millis
	 */
	public Delay(long millis) { _delayMillis=millis; }
	
	/**
	 * 
	 */
	public Delay() {}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#reify(com.zotoh.maedr.wflow.Step)
	 */
	@Override
	public FlowStep reify(FlowStep cur) {
		return reifyDelay(cur, this);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#realize(com.zotoh.maedr.wflow.Step)
	 */
	@Override
	public void realize(FlowStep cur) {
		DelayStep s= (DelayStep) cur;
		s.withDelay(_delayMillis);
	}
	
	/**
	 * @return
	 */
	public long getDelayMillis() { return _delayMillis; }
	
	/**
	 * @param millis
	 * @return
	 */
	public Delay withDelay(long millis) {
		_delayMillis=millis;
		return this;
	}
	
	
}

