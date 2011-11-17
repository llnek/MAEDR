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
  
package demo.fork.java;

import java.util.Properties;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.process.FWaitInfo;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 * 
child(process) ------> task1 -----> forkwait
                                                               |-----------------grand-child(process)  ----------> task1 ---> task2
                                                               |----(wait-till grand-child ends)
                                                               |----(resume)
                                                               |-------> task2  -----> end 
 */
public class ChildProcessor extends Stateless {

    private Object _res;

    public ChildProcessor(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        
        switch (getCurStepPos()) {
            case 10001:  return new Task1(this);
            
            case 10002: return new Task2(this);
            
            case 10003: return new Task3(this);
        }
        
        return WorkUnit.NONE;
    }

    /* pass data onto child process */
    protected void onSetDataLink(Processor child, Properties p)    {
        p.put("lhs", 5);
        p.put("rhs", 6);
    }
    
    /* receive data from child process when child ends */
    protected void onChildGiveback(Processor child, Properties p)    {
        _res= p.get("result");
    }
    
    @Override
    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }

    private class Task1 extends WorkUnit {
        public Task1(Processor p) { super(p, "task-1"); }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            System.out.println("Child: hello i am the child.");
            return new FlowInfo(10002);
        }
    }

    private class Task2 extends WorkUnit {    
        public Task2(Processor proc) {            super(proc, "task-2");        }
        @Override
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
        	// block and wait for grand-child to do its stuff and returns before resuming
            return new FWaitInfo(true, 10003, GChildProcessor.class);
        }        
    }

    private class Task3 extends WorkUnit {        
        public Task3(Processor proc) {            super(proc,"task-3");        }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            System.out.println("Child: the result for (5 * 6) according to my grand-child is = " + _res);
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            return FlowInfo.END;
        }        
    }

}
