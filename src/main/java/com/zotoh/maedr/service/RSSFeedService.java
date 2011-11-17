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

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.FeedEvent;
import com.zotoh.maedr.device.FeedIO;

/**
 * @author kenl
 *
 */
public class RSSFeedService extends TimerMulti<RSSFeedService> {
    
    private RSSFeedHandler _hdlr;
    
    /**
     * @return
     */
    public static RSSFeedService create() {
        return new RSSFeedService();
    }
    
    /**
     * @param h
     * @return
     */
    public RSSFeedService handler(RSSFeedHandler h) {
        _hdlr=h;
        return this;
    }
    
    /**
     * @param url
     * @return
     */
    public RSSFeedService feedUrl(String url) {
        JSONArray arr = _props.optJSONArray("urls");
        arr.put(url);
        return this;
    }
    
    private RSSFeedService() {        
        super(60);
        safePutProp("urls", new JSONArray());
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> m) throws Exception {
        return new FeedIO(m);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#getCB()
     */
    @Override
    public ServiceCB<FeedEvent> getCB() {
        return new ServiceCB<FeedEvent>() {
            public void handleEvent(FeedEvent ev) {
                _hdlr.eval(ev);
            }
            public Class<FeedEvent> getEventType() {
                return FeedEvent.class;
            }            
        };
    }
    
    
    
    
    
}