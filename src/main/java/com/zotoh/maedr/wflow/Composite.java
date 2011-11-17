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

import static com.zotoh.core.util.LangUte.LT;

import java.util.List;

/**
 * @author kenl
 *
 */
public abstract class Composite extends Activity {
	
	private List<Activity>  _children = LT();

	/**
	 * @return
	 */
	public int size() { return _children.size(); }
	
	/**
	 * @param a
	 */
	protected void add(Activity a) { 
		_children.add(a);
		onAdd(a);
	}
	
	/**
	 * @param a
	 */
	protected void onAdd(Activity a) {}
	
	/**
	 * @param container
	 * @return
	 */
	protected IterWrapper reifyInnerSteps(FlowStep container) {
		return new IterWrapper(container, _children);
	}

}
