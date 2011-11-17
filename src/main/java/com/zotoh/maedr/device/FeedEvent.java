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


package com.zotoh.maedr.device;

import com.sun.syndication.feed.synd.SyndFeed;

/**
 * An event generated by the FeedIO device.  
 * The event carries the data read from a RSS Atom feed.
 *
 * @author kenl
 */
public class FeedEvent extends Event {

    private static final long serialVersionUID = 2269702982785021995L;
    private final SyndFeed _feed;
    private final String _uri;
    
    /**
     * @param dev
     * @param targetUri
     * @param feed
     */
    public FeedEvent(Device dev, String targetUri, SyndFeed feed ) {
        super(dev);
        _feed=feed;
        _uri= targetUri;
    }

    /**
     * @return
     */
    public SyndFeed getFeedData() { return _feed; }
    
    
    /**
     * @return
     */
    public String getTargetUri() { return _uri; }
    
}
