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

package com.zotoh.maedr.wflow;

import java.util.Map;

import com.zotoh.maedr.core.AppDelegate;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.DelegateFactory;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.service.ServiceIO;

/**
 * @author kenl
 *
 */
public class FlowDelegateFactory implements DelegateFactory<MiniWFlow, FlowStep> {

    private final Map<String,ServiceIO> _devs;
    
    /**
     * 
     */
    protected FlowDelegateFactory(Map<String,ServiceIO> devs) {
        _devs=devs;
    }
    
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.DelegateFactory#create(com.zotoh.maedr.core.AppEngine)
     */
    @Override
    public AppDelegate<MiniWFlow,FlowStep> create(
            AppEngine<MiniWFlow,FlowStep> eng) throws Exception {
        return new WFlowDelegate((FlowBaseEngine) eng) {
            public MiniWFlow newProcess(Job job) {
                return new AdhocFlow(job,                       
                        _devs.get(job.getEvent().getDevice().getId()).getCB() );
            }            
        };            
    }
    
    
}
    
