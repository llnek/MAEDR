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
public class PTask extends Activity {

	private Work _work;

	/**
	 * @param w
	 */
	public PTask(Work w) {
		_work=w;
	}
	
	/**
	 * 
	 */
	public PTask() {}
	
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#reify(com.zotoh.maedr.wflow.Step)
	 */
	public FlowStep reify(FlowStep cur) {
		return reifyPTask(cur, this);
	}
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#realize(com.zotoh.maedr.wflow.Step)
	 */
	public void realize(FlowStep cur) {
		PTaskStep s=(PTaskStep)cur;
		s.withWork(_work);
	}

	/**
	 * @return
	 */
	public Work getWork() { return _work; }
	
	/**
	 * @param w
	 */
	public PTask withWork(Work w) { _work=w; return this; }
	
	
}
