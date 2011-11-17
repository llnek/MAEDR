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
 * A For is treated sort of like a while with the test-condition being (i < upperlimit).
 * 
 * @author kenl
 *
 */
public class For extends While {

	private ForLoopCountExpr _loopCntr;
	
	/**
	 * @param body
	 */
	public For(Activity body) {
		withBody(body);
	}

	/**
	 * 
	 */
	public For() {}	
	
	/**
	 * @param c
	 * @return
	 */
	public For withLoopCount(ForLoopCountExpr c) { 
		_loopCntr=c; return this; 
	}

	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.While#reify(com.zotoh.maedr.wflow.Step)
	 */
	public FlowStep reify(FlowStep cur) {
		return reifyFor(cur, this);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.While#realize(com.zotoh.maedr.wflow.FlowStep)
	 */
	public void realize(FlowStep cur) {
		ForStep s= (ForStep)cur;
		super.realize(s);
		s.withTest(new ForLoopExpr(s, _loopCntr));
	}
	
}

/**
 * @author kenl
 *
 */
class ForLoopExpr implements BoolExpr {
	private ForLoopCountExpr _cnt;
	private boolean _started;
	private int _loop;
	private FlowStep _step;
	
	/**
	 * @param s
	 * @param c
	 */
	public ForLoopExpr(FlowStep s, ForLoopCountExpr c) {
		_step=s;
		_cnt=c;
		_started=false;
	}
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.BoolExpr#eval(com.zotoh.maedr.core.Job)
	 */
	public boolean eval(Job job) {
		try {
			if (!_started) {
				_loop=_cnt.eval(job);
				_started=true;
			}
			_step.tlog().debug("ForLoopExpr: loop {}", _loop);
			return _loop > 0;
		}
		finally {
			--_loop;
		}
	}
}


