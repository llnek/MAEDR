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

import java.util.Collections;
import java.util.Map;

import com.zotoh.maedr.core.Job;

/**
 * @author kenl
 *
 */
public class SwitchStep extends FlowStep {

	private Map<Object,FlowStep> _cs;
	private SwitchChoiceExpr _expr;
	private FlowStep _def;
	
	/**
	 * @param s
	 * @param a
	 */
	protected SwitchStep(FlowStep s, Activity a) {
		super(s, a);
	}

	/**
	 * @param cs
	 * @return
	 */
	public SwitchStep withChoices(Map<Object,FlowStep> cs) {
		_cs= cs; return this;
	}
	
	/**
	 * @param def
	 * @return
	 */
	public SwitchStep withDef(FlowStep def) {
		_def=def; return this;
	}
	
	/**
	 * @param e
	 * @return
	 */
	public SwitchStep withExpr(SwitchChoiceExpr e) {
		_expr=e; return this;
	}

	public Map<Object,FlowStep> getChoices() { return Collections.unmodifiableMap(_cs); }
	public FlowStep getDef() { return _def; }
	
	@Override
	public FlowStep eval(Job job) throws Exception {
	    Object c= popClosureArg();   // data pass back from previous async call?		
        Object m= _expr.eval(job);
		FlowStep a= m==null ? null : _cs.get(m);
		// if no match, try default?
		if (a == null) {
			a=_def;
		}
		if (a != null) {
			a.attachClosureArg(c) ;
		}
		realize();
		return a;		
	}

}
