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

package com.zotoh.maedr.process;

import com.zotoh.maedr.process.Processor;

/**
 * Base class for hints related to forking of processes.
 *
 * @author kenl
 */
abstract class FForkInfo extends FlowInfo {
    
    private static final long serialVersionUID = 2083696081392371663L;
    private final boolean _shareParentJob;

    /**
     * @param shareParentJob
     * @param nextStep
     * @param z
     */
    protected FForkInfo(boolean shareParentJob, int nextStep, Class<? extends Processor> z)    {
        super(nextStep, 0L, z);
        _shareParentJob= shareParentJob;
    }
    
    /**
     * @return
     */
    public boolean isJobShared() { return _shareParentJob; }

}
