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

/**
 * (Internal use only)
 *
 * @author kenl
 */
public class MemEvent extends Event {
    
    private static final long serialVersionUID = -5717960404006249L;
    private final Object _arg;
    
    /**
     * @param dev
     * @param closureArg
     */
    public MemEvent(Device dev, Object closureArg)    {
        super(dev); _arg= closureArg;
    }
    
    /**
     * @param dev
     */
    public MemEvent(Device dev)    {
        this(dev, null);
    }

    /**
     * @return
     */
    public Object getClosureArg() {        return _arg;    }
    
    
}
