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

package com.zotoh.maedr.service;


import java.util.Map;
import java.util.Properties;

import static com.zotoh.core.util.LangUte.*;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.IOConfigurator;
import com.zotoh.maedr.core.Module;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.device.DeviceManager;


/**
 * @author kenl
 *
 */
public abstract class ServiceGroup<T,R> implements Vars {

	protected final Map<String, ServiceIO> _devs= MP();
	
	/**
	 * 
	 */
	protected ServiceGroup() {}
	
	/**
	 * @param io
	 * @return
	 */
	public ServiceGroup<T,R> add(ServiceIO io) {
		_devs.put( io.getId(), io);
		return this;
	}
	
	/**
	 * 
	 */
	public void start() {
        try {
            start911();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }		
	}
	
	/**
	 * @param eng
	 * @throws Exception
	 */
	protected abstract void bindCfg(AppEngine<T,R> eng) throws Exception ;
	
    /**/
    @SuppressWarnings("unchecked")
	private void start911() throws Exception {
        Properties props= (Properties) System.getProperties().get(ENG_PROPS);
        Module<T,R> mm= (Module<T,R>) Module.getPipelineModule();
        AppEngine<T,R> eng= mm.newEngine();
        eng.load(props);
        bindCfg(eng);        
        eng.bind( new IOConfigurator<T,R>() {
            public void config(DeviceManager<T,R> m) throws Exception {
                for (ServiceIO io : _devs.values()) {
                	io.configDevice(m);
                }
            }            
        });
        
        eng.startService();
    }
    
	
}
