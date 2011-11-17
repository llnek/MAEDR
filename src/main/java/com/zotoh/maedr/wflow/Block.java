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
 * A logical block - sequence of connected activities.
 * 
 * @author kenl
 *
 */
public class Block extends Composite {
	
	/**
	 * @param a at least one activity.
	 * @param more optional more activities.
	 */
	public Block(Activity a,  Activity ... more) {
		add(a);
		for (int i=0; i < more.length; ++i) { add( more[i]); }
	}

	/**
	 * 
	 */
	public Block() {}
	
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#chain(com.zotoh.maedr.wflow.Activity)
	 */
	public Block chain(Activity a) {
		add(a);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#reify(com.zotoh.maedr.wflow.Step)
	 */
	public FlowStep reify(FlowStep cur) {
		return reifyBlock(cur, this);
	}
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#realize(com.zotoh.maedr.wflow.Step)
	 */
	public void realize( FlowStep cur) {
		BlockStep s= (BlockStep)cur;
		s.withSteps( reifyInnerSteps(cur));
	}
	
}
