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
  
package demo.async;

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
class AsyncServerFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            def t= new AsyncResumeToken( getCurStep() );
            
            println("/* Pretend to call a webservice which takes a long time (10secs),");
            println("- since the call is *async*, event loop is not blocked.");
            println("- When we get a *call-back*, then the normal processing will continue */");
            
            DummyAsyncWS ws= new DummyAsyncWS();
            ws.doLongAsyncCall(new AsyncCallback() {
                void onSuccess(result) {
                    println("CB: Got WS callback: onSuccess");
                    println("CB: Tell the scheduler to re-schedule the original process");
                    // use the token to tell framework to restart the idled process
                    t.resume(result);
                }
                void onError(Exception e) {
                    t.resume(e);
                }                
                void onTimeout() {
                    t.resume(new Exception("time out"));
                }                              
            });
            
            println("\n\n");
            println("+ Just called the webservice, the process will be *idle* until");
            println("+ the websevice is done.");
            println("\n\n");
            
            setResult( new AsyncWait());
        }
    };
    
    def task2= new Work() {
        def void eval(Job j, Object closureArg) {
            println("-> The result from WS is: " + closureArg);
            println("\nPRESS Ctrl-C anytime to end program.\n");              
        }
    };
    
    def AsyncServerFlow(Job j) {
        super(j);
    }

    def Activity onStart() {        
        return new PTask( task1).chain( new PTask(task2));
    }

    public static class Preamble extends MiniWFlow {
        def Preamble(Job j) { super(j); }
        def Activity onStart() {
            return new PTask( new Work() {
                void eval(Job job, Object closure) {
                    println("Demo calling an async java-api & resuming.");                    
                }
            });
        }
    }
    
}
