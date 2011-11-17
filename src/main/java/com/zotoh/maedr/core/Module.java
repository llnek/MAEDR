/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
 
package com.zotoh.maedr.core;

import java.util.Map;

import com.zotoh.core.util.MetaUte;
import com.zotoh.maedr.service.ServiceIO;

/**
 * @author kenl
 *
 * @param <T>
 */
public abstract class Module<T,D> implements Vars {
    
	/**
	 * @return
	 */
	public abstract String getDefDelegateClass();
	
	/**
	 * @return
	 */
	public abstract String getShortName();

	/**
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static Module<?,?> getPipelineModule() throws Exception {
    	return (Module<?,?>) MetaUte.loadClass(
    					System.getProperty(PIPLINE_MODULE))
    	.getConstructor().newInstance();
	}
	
    /**
     * @param devs
     * @return
     */
    public abstract DelegateFactory<T,D> newDelegateFactory(Map<String,ServiceIO> devs);
    
    /**
     * @return
     */
    public abstract AppEngine<T,D> newEngine();
    
    
}
