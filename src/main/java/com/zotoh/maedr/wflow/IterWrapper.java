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

import java.util.ArrayList;
import java.util.List;

/**
 * @author kenl
 *
 */
class IterWrapper {
	
	private final List<Activity> _acts;
	private final FlowStep _container;

	@SuppressWarnings("serial")
	public IterWrapper(final FlowStep c, final List<Activity> a) {
		_container=c;
		_acts= new ArrayList<Activity>() {{ 
			addAll(a);
		}};
	}
	
	public boolean isEmpty() {
		return _acts.size() == 0;
	}
	
	/**
	 * @return
	 */
	public FlowStep getNext() {
		return _acts.remove(0).reify(_container);
	}
	
}
