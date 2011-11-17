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

import static com.zotoh.core.util.CoreUte.*;

/**
 * @author kenl
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class TimerOne<T> extends ServiceIO {
     
     /**
     * @param delaysecs
     */
    protected TimerOne(int delaysecs) {
         safePutProp("delaysecs", delaysecs); 
     }
     
     /**
     * 
     */
    protected TimerOne() {
         this(0);
     }

    /**
     * @param secs
     * @return
     */
    public T delaysecs(int secs) {
        tstNonNegIntArg("seconds to delay activation", secs);
        safePutProp("delaysecs", secs);
        return (T) this;
    }
    
    /**
     * @param datetime  Either "yyyyMMdd" or "yyMMdd'T'HH:mm:ss"
     * @return
     */
    public T when(String datetime) {
        tstEStrArg("activation datetime", datetime);
        if ( datetime.matches("\\d{8}T\\d{2}:\\d{2}:\\d{2}") || datetime.matches("\\d{8}") ) {
            safePutProp("when", datetime);            
        }
        return (T) this;
    }
    
    
    
    
 }
 
 
 