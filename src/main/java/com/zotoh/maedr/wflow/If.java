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
 * @author kenl
 *
 */
public class If extends Conditional {

	private Activity _then, _else;
	
	/**
	 * @param expr
	 * @param thenCode
	 * @param elseCode
	 */
	public If(BoolExpr expr, Activity thenCode, Activity elseCode) {
		super(expr);
		_then= thenCode;
		_else= elseCode;
	}
	
	/**
	 * @param expr
	 * @param thenCode
	 */
	public If(BoolExpr expr, Activity thenCode) {
		this(expr, thenCode, null);
	}
	
	/**
	 * 
	 */
	public If() {}
	
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#reify(com.zotoh.maedr.wflow.Step)
	 */
	public FlowStep reify(FlowStep cur) {
		return reifyIf(cur, this);
	}
	
	/**
	 * @param elseCode
	 * @return
	 */
	public If withElse(Activity elseCode) {
		_else=elseCode;
        return this;
	}
	
	/**
	 * @param thenCode
	 * @return
	 */
	public If withThen(Activity thenCode) {
		_then=thenCode;
        return this;
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.Activity#realize(com.zotoh.maedr.wflow.Step)
	 */
	public void realize(FlowStep cur) {
		FlowStep next= cur.getNextStep();
		IfStep s= (IfStep)cur;		
		s.withElse( _else ==null ? next : _else.reify(next) );
		s.withThen( _then.reify(next));
		s.withTest( getExpr());
	}

}
