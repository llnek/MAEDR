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
  
package demo.pop3.java;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.POP3Event;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class POP3ServerProcessor extends Stateless {

	private static int _count=0;
	
	/**/
    public POP3ServerProcessor(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
        case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    @Override
    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }
    
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
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
            
            return FlowInfo.END;
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
