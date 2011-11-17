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
public class Split extends Composite {

	protected Join _theJoin, _join;
	
    /**
     * 
     */
    public Split(Join j) {
        _join=j;
    }

    /**
     * 
     */
    public Split() {}
    
    
	/**
	 * @param a
	 * @return
	 */
	public Split addSplit(Activity a) {
		add(a); 
        return this;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#reify(com.zotoh.maedr.wflow.Step)
	 */
	public FlowStep reify(FlowStep cur) {
		return reifySplit(cur, this); 
	}

	/**
	 * @param a
	 * @return
	 */
	public Split withJoin(Join a) {
		_join=a;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#realize(com.zotoh.maedr.wflow.Step)
	 */
	public void realize(FlowStep cur) {
		if ( _join!=null) {
			_join.withBranches( size());
			_theJoin = _join;
		} else {
			_theJoin= new NullJoin();
		}
		FlowStep s = _theJoin.reify(cur.getNextStep());
		SplitStep ss= (SplitStep)cur;
		if (_theJoin instanceof NullJoin) {
			ss.fallThrough();
		}
		ss.withBranches(reifyInnerSteps(s));
	}	
		

}

/**
 * @author kenl
 *
 */
class NullJoin extends Join {
	
	/**
	 * 
	 */
	public NullJoin() {}

	public FlowStep reify(FlowStep cur) {
		return reifyNullJoin(cur, this);
	}

	public void realize(FlowStep cur) {}	
	
}

/**
 * @author kenl
 *
 */
class NullJoinStep extends JoinStep {

	/**
	 * @param s
	 * @param a
	 */
	public NullJoinStep(FlowStep s, Join a) {
		super(s, a);
	}

	@Override
	public FlowStep eval(Job job) {
		return null;
	}
	
}
