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

package com.zotoh.maedr.process;


/**
 * This is used as a hint to the runtime that an async call has been made, so place (the processor) on hold in the meantime , and
 * upon resumption, invoke (the processor) again at this *nextstep*.
 *
 * @author kenl
 */
public class FAsyncWaitInfo extends FlowInfo {

    private static final long serialVersionUID = -7959316617262411309L;

    /**
     * @param nextStep
     */
    public FAsyncWaitInfo(int nextStep)    {
        super(nextStep, -1L, (Object)null);
    }
    
}
