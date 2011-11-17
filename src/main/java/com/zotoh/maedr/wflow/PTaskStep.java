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

import com.zotoh.maedr.core.Job;

/**
 * @author kenl
 *
 */
public class PTaskStep extends FlowStep {

	private Work _work;

	/**
	 * @param s
	 * @param a
	 */
	public PTaskStep(FlowStep s, PTask a) {
		super(s,a);
	}

	/**
	 * @param w
	 * @return
	 */
	public PTaskStep withWork(Work w) {
		_work=w;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.FlowStep#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) throws Exception {
		Activity a= _work.perform(this, job);
		FlowStep rc= getNextStep();
		
		if (a != null) {
			if (a instanceof Nihil) { rc = reifyZero( getFlow()); }
			else {	
				rc= a.reify(rc);
			}
		}
		return rc;
	}


}
