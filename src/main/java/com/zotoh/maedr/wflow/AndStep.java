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
 * A "AND" join enforces that all bound activities must return before Join continues.
 * 
 * @author kenl
 *
 */
public class AndStep extends JoinStep {

	/**
	 * @param s
	 * @param a
	 */
	protected AndStep(FlowStep s, And a) {
		super(s,a);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) {		return eval_0(job); 	}
	
	
	private synchronized FlowStep eval_0(Job job) {
	    Object c= popClosureArg();
		FlowStep rc= null;
		
		++_cntr;
		tlog().debug("AndStep: size={}, cntr={}, join={}", size(), _cntr, this);
		
		if (_cntr == size()) {
			// all branches have returned, proceed...
			rc= _body == null ? getNextStep() : _body;
			if (rc != null) { rc.attachClosureArg(c); }
			realize();
		} 
		
		return rc; 		
	}

}
