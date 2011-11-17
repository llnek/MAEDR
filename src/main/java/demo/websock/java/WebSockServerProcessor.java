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
  

package demo.websock.java;

import com.zotoh.core.util.CoreUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.netty.WebSockEvent;
import com.zotoh.maedr.device.WebSockResult;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 *
 */
public class WebSockServerProcessor extends Stateless {

    public WebSockServerProcessor(Job j) {
        super(j);
    }

    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            WebSockEvent ev= (WebSockEvent) job.getEvent();
            String msg= ev.getText();
            System.out.println( "Message: square this number: " + msg );
            int i= CoreUte.asInt(msg, 0);
            i=i*i;
            msg= Integer.toString(i) ;
            WebSockResult res= new WebSockResult();
            res.setData(msg);
            ev.setResult(res);
            
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            
            return FlowInfo.END;
        }
        
    }
    
    
}
