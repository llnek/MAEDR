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
 
package com.zotoh.maedr.device;

import static com.zotoh.core.util.LoggerFactory.getLogger;

import static com.zotoh.core.util.CoreUte.*;
import com.zotoh.core.util.Logger;

/**
 * Base class for all other triggers, handling asynchronous results from
 * the application.
 * 
 * @author kenl
 */
public abstract class AsyncTrigger implements AsyncWaitTrigger {
    
    private Logger ilog() {  return _log=getLogger(AsyncTrigger.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }
    
    private final Device _dev;
    
    /**
     * @param dev 
     */
    protected AsyncTrigger(Device dev) { tstObjArg("device", dev); _dev=dev; }
        
    /**
     * @return
     */
    protected Device dev() { return _dev; }
    
}
