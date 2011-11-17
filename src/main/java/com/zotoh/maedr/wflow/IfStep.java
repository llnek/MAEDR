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
public class IfStep extends ConditionalStep {

	private FlowStep _then, _else;

	/**
	 * @param s
	 * @param a
	 */
	public IfStep(FlowStep s, If a) {
		super(s,a);
	}

	/**
	 * @param s
	 * @return
	 */
	public IfStep withElse(FlowStep s) {
		_else=s;
        return this;
	}

	/**
	 * @param s
	 * @return
	 */
	public IfStep withThen(FlowStep s) {
		_then=s;
        return this;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Step#eval(com.zotoh.maedr.core.Job)
	 */
	public FlowStep eval(Job job) {
	    Object c= popClosureArg();   // data pass back from previous async call?
		boolean b = test(job);
		tlog().debug("If: test {}", ( b ? "OK" : "FALSE"));
		FlowStep rc = b ? _then : _else;
		if (rc != null) {
		    rc.attachClosureArg(c);
		}
		realize();
		return rc;
	}

}
