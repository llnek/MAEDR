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
  
package demo.jms;

import javax.jms.Message;
import javax.jms.TextMessage;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.JmsEvent;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


/**
 * @author kenl
 *
 */
class JMSRecvrFlow extends MiniWFlow {

	private static def _count=0;
	
	def task1= new Work() {
	    void eval(Job job, Object closure) {
            def ev= job.getEvent();
            def msg= ev.getMsg();

            println("-> Correlation ID= " + msg.getJMSCorrelationID());
            println("-> Msg ID= " + msg.getJMSMessageID());
            println("-> Type= " + msg.getJMSType());
            
            if (msg instanceof TextMessage) {
                println("-> Text Message= " + msg.getText());
            }
            
            ++_count;
            
            if (_count > 3) {
                    println("\nPRESS Ctrl-C anytime to end program.\n");
            }
            	        
	    }
	};
	
    def JMSRecvrFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask(task1);
    }
    
    
    
    
    
    
    
    
    public static class Preamble extends MiniWFlow {
        def Preamble(Job j) { super(j); }
        def Activity onStart() {
            return new PTask( new Work() {
                void eval(Job job, Object closure) {
                    println("Demo receiving JMS messages..." );
                }
            });
        }
    }
    
    
    
    
    
    
    
    
    
    
}
