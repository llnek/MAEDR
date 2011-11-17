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
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.AsyncResumeToken;
import com.zotoh.maedr.wflow.AsyncWait;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 *
 */
public class AsyncServerFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) {
            final AsyncResumeToken t= new AsyncResumeToken( getCurStep() );
            
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
                public void onError(Exception e) {
                    t.resume(e);
                }                
                public void onTimeout() {
                    t.resume(new Exception("time out"));
                }                              
            });
            
            System.out.println("\n\n");
            System.out.println("+ Just called the webservice, the process will be *idle* until");
            System.out.println("+ the websevice is done.");
            System.out.println("\n\n");
            
            setResult( new AsyncWait());
        }
    };
    
    Work task2= new Work() {
        public void eval(Job j, Object closureArg) {
            System.out.println("-> The result from WS is: " + closureArg);
            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");              
        }
    };
    
    public AsyncServerFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {        
        return new PTask( task1).chain( new PTask(task2));
    }


    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Demo calling an async java-api & resuming.");                    
                }
            });
        }
    }
    
}
