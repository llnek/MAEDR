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
 * The FlowInfo class is a data structure which carries a set of *hints* for the runtime scheduler.  Without these hints,
 * the runtime would not be able to correctly schedule the processor.
 * (1) the next step inside the process flow
 * (2) any specific core(thread-group) to schedule in
 * (3) any delays before the next schedule
 * (4) any opaque object (closure) to pass to the processor (next step) 
 *
 * @author kenl
 */
public class FlowInfo implements java.io.Serializable {
	
    private static final long serialVersionUID = -5962699512042767201L;
    private final Object _closureObject;
    private final String _nextQueue;
    private final long _delayMillis;
    private final int _nextStep;
    
    /*
     * Indicates that no more processing is needed and the runtime can end the
     * processor.
     * */
    public static final FlowInfo END= new FlowInfo(Processor.END,0L, null);
    
    /**
     * @param nextQ
     * @param nextStep
     * @param delayMillis
     * @param closureArg
     */
    public FlowInfo( String nextQ, int nextStep, long delayMillis, Object closureArg ) {
        _closureObject=closureArg;
        _nextQueue= nextQ;
        _nextStep=nextStep;
        _delayMillis=delayMillis;
    }

    /**
     * @param nextStep
     * @param delayMillis
     * @param closureArg
     */
    public FlowInfo( int nextStep, long delayMillis, Object closureArg ) {
        this("", nextStep, delayMillis, closureArg);
    }
    
    /**
     * @param nextStep
     */
    public FlowInfo( int nextStep) {
        this(nextStep, 0L, null) ;
    }
    
    /**
     * @param nextQ
     * @param nextStep
     */
    public FlowInfo( String nextQ, int nextStep) {
        this(nextQ, nextStep, 0L, null);
    }
    
    /**
     * @return
     */
    public String getNextQueue() {        return _nextQueue;    }
    
    /**
     * @return
     */
    public long getDelayMillis() {        return _delayMillis;    }
    
    /**
     * @return
     */
    public int getNextStep() {        return _nextStep;    }
    
    /**
     * @return
     */
    public Object getClosureArg() {        return _closureObject;    }
    
    
}
