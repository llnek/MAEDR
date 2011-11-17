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
package com.zotoh.maedr.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.JettyIO;
import com.zotoh.maedr.device.ServletEvent;
import com.zotoh.maedr.device.ServletEventResult;


/**
 * @author kenl
 *
 */
public class JettyWebService extends BasicHTTP<JettyWebService> {

	private JettyWebHandler _hdlr;
	
	/**
	 * @param port
	 * @return
	 */
	public static JettyWebService create(int port) {
		return new JettyWebService(port);
	}
	
	/**
	 * @param h
	 * @return
	 */
	public JettyWebService handler(JettyWebHandler h) {
		_hdlr=h;
		return this;
	}
	
	/**
	 * @param ctx
	 * @return
	 */
	public JettyWebService contextPath(String ctx) {
		safePutProp("contextpath", ctx);
		return this;
	}
	
	/**
	 * @param baseDir
	 * @return
	 */
	public JettyWebService resourceBase(String baseDir) {
		safePutProp("resbase", baseDir);
		return this;
	}
	
	/**
	 * @param servletPaths
	 * @return
	 */
	public JettyWebService servletPaths(String... paths) {
		if (paths != null) for (int i=0; i < paths.length; ++i) {
			_props.optJSONArray("urlpatterns").put(paths[i]);			
		}
		return this;
	}
	
	/**
	 * @param path
	 * @param filterClass
	 * @param params
	 * @return
	 */
	public JettyWebService filter(String path, String filterClass, String... params) {
		int len= params != null ? params.length : 0;
		int pos=0;
		JSONObject p, obj= new JSONObject();
		try {
			obj.put("urlpattern", path);
			obj.put("class", filterClass);
			p=new JSONObject();
			obj.put("params", p);
			if (len>0 && (len % 2 )==0) {
				while (pos < len) {
					p.put(params[pos], params[pos+1]) ;
					pos+=2;
				}
			}
			_props.optJSONArray("filters").put(obj);
		}
		catch (JSONException e) 
		{}
		return this;
	}
	
	private JettyWebService(int port) {
		super(port);
		safePutProp("urlpatterns", new JSONArray());
		safePutProp("filters", new JSONArray());
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
	 */
	@Override
	protected Device newDevice(DeviceManager<?,?> m) throws Exception {
		return new JettyIO(m);
	}

	@Override
	public ServiceCB<ServletEvent> getCB() {
		return new ServiceCB<ServletEvent>() {
			public void handleEvent(ServletEvent ev) {
				_hdlr.eval(ev, new ServletEventResult());
			}
			public Class<ServletEvent> getEventType() {
				return ServletEvent.class;
			}			
		};
	}

}
