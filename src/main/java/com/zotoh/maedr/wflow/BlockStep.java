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
public class BlockStep extends FlowStep {

	protected IterWrapper _steps;

	/**
	 * @param s
	 * @param a
	 */
	protected BlockStep(FlowStep s, Block a) {
		super(s, a);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) throws Exception {
        Object c= popClosureArg();  // data pass back from previous async call?
		FlowStep n, rc= null;

		if ( ! _steps.isEmpty()) {
		    n=_steps.getNext();
		    n.attachClosureArg(c);
			rc = n.eval(job);
		} else {
			rc=getNextStep();
            if (rc != null) {  rc.attachClosureArg(c); }
			realize();
		}
		
		return rc;
	}
	
	/**
	 * @param wrap
	 * @return
	 */
	public BlockStep withSteps(IterWrapper wrap) {
        _steps=wrap;
        return this;
	}	

	
}
