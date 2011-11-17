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
public class SplitStep extends FlowStep {

	private IterWrapper _branches;
	private boolean _fallThru;
	
	/**
	 * @param s
	 * @param a
	 */
	public SplitStep(FlowStep s, Split a) {
		super(s,a);
	}
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) {
		Object c= popClosureArg();
		FlowStep rc;
		
		while ( !_branches.isEmpty()) {
			rc = _branches.getNext();
			rc.attachClosureArg(c);
			getFlow().getScheduler().run(rc.getCore(),rc);			
		}
		
		realize();

		// should we also pass the closure to the next step ? not for now
		rc= _fallThru ? getNextStep() : null;
		return rc;
	}
	
	/**
	 * @param w
	 * @return
	 */
	public SplitStep withBranches(IterWrapper w) {
		_branches=w;
		return this;
	}
	
	/**
	 * @return
	 */
	public SplitStep fallThrough() {
		_fallThru=true;
		return this;
	}
	
}
