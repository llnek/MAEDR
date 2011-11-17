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
  
package demo.timer.java;

import java.util.Date;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.TimerEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class TimeServerProcessor extends Stateless {
    private static int _count=0;
    
    public TimeServerProcessor(Job j) {
        super(j);
    }
    
    protected WorkUnit getNextWorkUnit() {
        
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        
        return WorkUnit.NONE;
    }
        
    protected FlowInfo onStart(Event ev) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }


    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {            super(proc, "task1");        }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            TimerEvent ev= (TimerEvent) job.getEvent();                
            if ( ev.isRepeating()) {
                if (_count < 5) {
                    System.out.println("-----> repeating-update: " + new Date()) ;
                    ++_count;
                }
                if (_count >= 5) {
    					System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
                }
            }
            else {
                System.out.println("-----> once-only!!: " + new Date()) ;            
            }
            
            return FlowInfo.END;
        }
        
    }





}
