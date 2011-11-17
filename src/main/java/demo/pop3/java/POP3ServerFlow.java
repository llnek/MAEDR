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
  
package demo.pop3.java;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.POP3Event;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


/**
 * @author kenl
 *
 */
public class POP3ServerFlow extends MiniWFlow {

	private static int _count=0;
	
	Work task1= new Work() {
	    public void eval(Job job, Object closure) throws Exception {
            POP3Event e= (POP3Event) job.getEvent();
            byte[] bits=e.getMsg().getBytes();
            System.out.println("########################");
            System.out.print(e.getSubject() + "\r\n");
            System.out.print(e.getFrom() + "\r\n");
            System.out.print(e.getTo() + "\r\n");
            System.out.print("\r\n");
            System.out.println(new String(bits,"utf-8"));
            
            ++_count;
            
            if (_count > 3) {
                    System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            }
	    }
	} ;
	
	/**/
    public POP3ServerFlow(Job j) {
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
                    System.out.println("Demo receiving POP3 emails..." );
                }
            });
        }
    }
    
    
    
    
    
    
}
