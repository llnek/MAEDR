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

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.process.FSplitInfo;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 * 
    parent(process) ----> task1 --> fork&split 
                                                   |-------------------------------------> child(process) ----> task1 --> task2 ....
                                                   |
                                                   |-------> task2 ----> end
 */
public class ParentProcessor extends Stateless {
    
	public ParentProcessor(Job j) {
		super(j);
	}

	@Override
	protected WorkUnit getNextWorkUnit() {
	    
	    switch (getCurStepPos()) {
	    
	        case 10001: return new WorkUnit(this,"task1") {
                @Override
                protected FlowInfo evalOneStep(Job job, Object closureArg)
                        throws Exception {
                    System.out.println("Parent: I am the parent.");
                    return new FlowInfo(10002);
                }	            
	        };
	        
            	        
	        case 10002: return new Task2(this); 
	        
            case 10003: return new Task3(this); 
	    }
	    
		return WorkUnit.NONE;
	}

	@Override
	protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
	}

	@Override
	protected void onEnd() {
	    System.out.println("Parent process: the-end");
	}

	@Override
	protected FlowInfo onError(Exception e) {
		e.printStackTrace();
		return null;
	}	
	

	private class Task2 extends WorkUnit {

        public Task2(Processor proc) {
            super(proc,"task2");
        }
    
        @Override
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            // after forking the child process, parent continues with this as the next step
            return new FSplitInfo(true, 10003, ChildProcessor.class);
        }
    
	}

	private class Task3 extends WorkUnit {

            public Task3(Processor proc) {
                super(proc, "task3");
            }
        
            @Override
            protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
                System.out.println("Parent: to calculate fib(6)...");
                System.out.print("Parent: ");
                for (int i=1; i <= 6; ++i) {
                    System.out.print( fib(i) + " ");
                }
                System.out.println();
                return FlowInfo.END;    // indicate end is next
            }
            
            private int fib(int n) {
                if (n <3) return 1;
                return fib(n-2) + fib(n-1);
            }
    
	}








}
