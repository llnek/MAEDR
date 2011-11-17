/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import org.json.JSONObject;

/**
 * (Internal use only)
 *
 * @author kenl
 */
public class MemDevice extends Device {
    
    /**
     * @param mgr
     */
    public MemDevice(DeviceManager<?,?> mgr)    {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#inizWithProperties(org.json.JSONObject)
     */
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception    {
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    @Override
    protected void onStart() throws Exception    {}
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    @Override
    protected void onStop()    {}
    
}
