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

import static com.zotoh.core.util.LoggerFactory.getLogger;

import com.zotoh.core.util.Logger;

/**
 * An Activity is a definition of work - a task to be done.
 * At runtime, it has to be reified - make alive.  This process
 * turns an Activity into a Step in the Workflow.
 * 
 * @author kenl
 *
 */
public abstract class Activity {
	
    private Logger ilog() {  return _log=getLogger(Activity.class);    }
    private transient Logger _log= ilog();
    
    /**
     * @return
     */
    public Logger tlog() {  return _log==null ? ilog() : _log;    }
    
	/**
	 * 
	 */
	protected Activity() {}
	
	/**
	 * Connect up another activity to make up a chain.
	 * 
	 * @param a the unit of work to follow after this one.
	 * @return an *ordered* list of work units.
	 */
	public Block chain( Activity a) {		return new Block(this, a); 	}
	
	/**
	 * Instantiate a *live* version of this work unit as it becomes
	 * part of the Workflow.
	 * 
	 * @param cur current step.
	 * @return a *live* version of this Activity.
	 */
	public abstract FlowStep reify(FlowStep cur);
	
	/**
	 * Configure the *live* version of this Activity.
	 * 
	 * @param cur
	 */
	public abstract void realize(FlowStep cur);

}
