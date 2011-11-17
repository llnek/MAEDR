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

package com.zotoh.maedr.process;

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
public class ProcDelegateFactory implements DelegateFactory<Processor, Processor> {

    private final Map<String,ServiceIO> _devs;
    
    /**
     * 
     */
    protected ProcDelegateFactory(Map<String,ServiceIO> devs) {
        _devs=devs;
    }
    
    
    @Override
    public AppDelegate<Processor, Processor> create(
            AppEngine<Processor, Processor> eng) throws Exception {
        return new ProcDelegate((ProcBaseEngine) eng) {
            public Processor newProcess(Job job) {
                return new AdhocProcessor(job,                       
                        _devs.get(job.getEvent().getDevice().getId()).getCB() );
            }            
        };            
    }
    
    
}
    
