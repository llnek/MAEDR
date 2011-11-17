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

import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.maedr.wflow.Reifier.reifySwitch;

import java.util.Map;


/**
 * @author kenl
 *
 */
public class Switch extends Activity {
	
	private Map<Object,Activity> _choices= MP();
	private Activity _def;
	private SwitchChoiceExpr _expr;
	
	/**
	 * 
	 */
	public Switch(SwitchChoiceExpr e) {
		_expr=e;
	}

	/**
	 * 
	 */
	public Switch() {}
	
	
	/**
	 * @param e
	 * @return
	 */
	public Switch withExpr(SwitchChoiceExpr e) { _expr=e; return this; }
	
	/**
	 * @param matcher
	 * @param body
	 * @return
	 */
	public Switch withChoice(Object matcher, Activity body) {
		_choices.put(matcher, body);
		return this;
	}

	/**
	 * @param a
	 * @return
	 */
	public Switch withDef(Activity a) {
		_def=a;
		return this;
	}
	
	@Override
	public FlowStep reify(FlowStep cur) {
		return reifySwitch(cur, this);
	}

	@Override
	public void realize(FlowStep cur) {

		FlowStep next= cur.getNextStep();
		SwitchStep s= (SwitchStep) cur;
		Map<Object,FlowStep> t= MP();
		
		for (Map.Entry<Object,Activity> en : _choices.entrySet()) {			
			t.put(en.getKey(),
							en.getValue().reify(next));
		}		
		s.withChoices(t);
		if (_def != null) {
			s.withDef( _def.reify(next));
		}
		s.withExpr(_expr);
	}
	
	
	
	
	
	
	
	
}
