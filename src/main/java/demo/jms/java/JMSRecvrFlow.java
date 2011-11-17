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
  
package demo.jms.java;

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
public class JMSRecvrFlow extends MiniWFlow {

	private static int _count;
	
	Work task1= new Work() {
	    public void eval(Job job, Object closure) throws Exception {
            JmsEvent ev= (JmsEvent) job.getEvent();
            Message msg= ev.getMsg();

            System.out.println("-> Correlation ID= " + msg.getJMSCorrelationID());
            System.out.println("-> Msg ID= " + msg.getJMSMessageID());
            System.out.println("-> Type= " + msg.getJMSType());
            
            if (msg instanceof TextMessage) {
                TextMessage t= (TextMessage)msg;
                System.out.println("-> Text Message= " + t.getText());
            }
            
            ++_count;
            
            if (_count > 3) {
                    System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            }
            	        
	    }
	};
	
    public JMSRecvrFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new PTask(task1);
    }
    
    
    
    
    
    
    
    
    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Demo receiving JMS messages..." );
                }
            });
        }
    }
    
    
    
    
    
    
    
}
