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
public class OrStep extends JoinStep {
	
	/**
	 * @param s
	 * @param a
	 */
	public OrStep(FlowStep s, Or a) {
		super(s,a);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) {
		return eval_0(job);
	}

	private synchronized FlowStep eval_0(Job job) {
	    Object c= popClosureArg();
		FlowStep rc= this;
		
		++_cntr;
		
		if (size()==0) {
            rc= getNextStep();
			realize();
			if (rc != null) { rc.attachClosureArg(c); }
			return rc;
		}
		
		if (_cntr==1) {
			rc= _body== null ? getNextStep() : _body;
		} 
		else if ( _cntr==size()){
			rc=null;
			realize();
		}
				
        if (rc != null) { rc.attachClosureArg(c); }
		return rc;
	}
	
}
