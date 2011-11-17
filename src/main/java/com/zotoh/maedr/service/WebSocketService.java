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

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.WebSockResult;
import com.zotoh.maedr.device.netty.WebSockEvent;
import com.zotoh.maedr.device.netty.WebSockIO;

 
/**
 * @author kenl
 *
 */
public class WebSocketService extends BasicHTTP<WebSocketService> {

	private WebSocketHandler _hdlr;
	
	/**
	 * @param port
	 * @return
	 */
	public static WebSocketService create(int port) {
		return new WebSocketService(port);
	}

	/**
	 * @param h
	 * @return
	 */
	public WebSocketService handler(WebSocketHandler h) {
		_hdlr=h;
		return this;
	}
	
	/**
	 * @param uri
	 * @return
	 */
	public WebSocketService uriPattern(String uri) {
		safePutProp("uri", uri);
		return this;
	}
	
	/**
	 * @param port
	 */
	private WebSocketService(int port) {
		super(port);
	}

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
	 */
	@Override
	protected Device newDevice(DeviceManager<?,?> m) throws Exception {
		return new WebSockIO(m);
	}

	@Override
	public ServiceCB<WebSockEvent> getCB() {
		return new ServiceCB<WebSockEvent>() {
			public void handleEvent(WebSockEvent ev) {
				_hdlr.eval(ev, new WebSockResult());
			}
			public Class<WebSockEvent> getEventType() {
				return WebSockEvent.class;
			}			
		};
	}

}
