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
  
package demo.async.java;


import com.zotoh.maedr.core.AsyncCallback;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.process.AsyncResumeToken;
import com.zotoh.maedr.process.FAsyncWaitInfo;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 *
 */
public class AsyncServerProcessor extends Stateless {

    private class Task1 extends WorkUnit {
    	
        public Task1(Processor proc) {
            super(proc, "task-1");
        }
        
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            final AsyncResumeToken t= new AsyncResumeToken(getProcessor());
            
            System.out.println("/* Pretend to call a webservice which takes a long time (10secs),");
            System.out.println("- since the call is *async*, event loop is not blocked.");
            System.out.println("- When we get a *call-back*, then the normal processing will continue */");
            
            DummyAsyncWS ws= new DummyAsyncWS();
            ws.doLongAsyncCall(new AsyncCallback() {
                public void onSuccess(Object result) {
                    System.out.println("CB: Got WS callback: onSuccess");
                    System.out.println("CB: Tell the scheduler to re-schedule the original process");
                    // use the token to tell framework to restart the idled process
                    t.resume(result);
                }
                public void onError(Exception e) {}                
                public void onTimeout() {}                              
            });
            
            System.out.println("\n\n");
            System.out.println("+ Just called the webservice, the process will be *idle* until");
            System.out.println("+ the websevice is done.");
            System.out.println("\n\n");
            
            // after we've gotten the callback, resume at this next step
            return new FAsyncWaitInfo(10002);
        }        
    }
    
    private class Task2 extends WorkUnit {
        public Task2(Processor proc) {
            super(proc, "task-2");
        }
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            System.out.println("-> The result from WS is: " + closureArg);
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            return FlowInfo.END;
        }
    }
    
    public AsyncServerProcessor(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
            case 10002: return new Task2(this);
        }
        return WorkUnit.NONE;
    }

    @Override
    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
        return new FlowInfo(10001);
    }
    
}