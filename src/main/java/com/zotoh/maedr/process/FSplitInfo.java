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

import com.zotoh.maedr.process.Processor;

/**
 * Spin off another process separately, but this process continues as before. 
 *
 * @author kenl
 */
public class FSplitInfo extends FForkInfo {
	
    private static final long serialVersionUID = -7847542822959151814L;
    
    /**
     * @param shareParentJob
     * @param nextStep
     * @param z
     */
    public FSplitInfo(boolean shareParentJob, int nextStep, Class<? extends Processor> z)    {
        super(shareParentJob, nextStep, z);
    }
    
    
}
