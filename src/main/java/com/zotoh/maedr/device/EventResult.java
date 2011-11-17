/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import com.zotoh.core.util.Logger;

import static com.zotoh.core.util.LoggerFactory.getLogger;

/**
 * Base class for results which are to be sent back to the client.
 * 
 * @author kenl
 */
public abstract class EventResult implements java.io.Serializable {
    
    private Logger ilog() {       return _log=getLogger(EventResult.class);    }
    private static final long serialVersionUID= -874578359743L;
    private transient Logger _log= ilog();    
    public Logger tlog() {         return _log==null ? ilog() : _log;    }    
    private boolean _hasError;
    
    /**
     * @return
     */
    public boolean hasError() { return _hasError; }
    
    /**
     * @param b
     */
    protected void setError(boolean b) {        _hasError=b;    }
    
    
    /**
     * 
     */
    protected EventResult()
    {}
    
    
}
