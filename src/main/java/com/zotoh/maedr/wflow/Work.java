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
public abstract class Work {

    private FlowStep _curStep;
	private Activity _result;
	
	/**
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public Activity perform(FlowStep cur, Job job) throws Exception {
	    _curStep=cur;
	    _result=null;
		eval(job, cur.popClosureArg());
		return _result;
	}
	
	/**
	 * @param job
	 * @param arg
	 * @throws Exception
	 */
	protected abstract void eval(Job job, Object arg) throws Exception;

	/**
	 * @param a
	 */
	protected void setResult(Activity a) {
		_result=a;
	}
	
	/**
	 * @return
	 */
	protected FlowStep getCurStep() {
	    return _curStep;
	}
	
	
}
