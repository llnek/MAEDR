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


package com.zotoh.maedr.process;

import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.service.ServiceGroup;

/**
 * @author kenl
 *
 */
public class ProcServiceGroup extends ServiceGroup<Processor,Processor> {

	/**
	 * 
	 */
	public ProcServiceGroup() {}
		
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.service.ServiceGroup#bindCfg(com.zotoh.maedr.core.AppEngine)
	 */
	@Override
	protected void bindCfg(AppEngine<Processor, Processor> eng)
			throws Exception {
        eng.bindDelegateFactory(eng.getModule().newDelegateFactory(_devs));
	}
	
}
