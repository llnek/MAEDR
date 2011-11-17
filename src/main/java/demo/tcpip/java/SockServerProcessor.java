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
  
package demo.tcpip.java;

import java.io.BufferedInputStream;
import java.io.InputStream;

import com.zotoh.core.util.ByteUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.TCPEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class SockServerProcessor extends Stateless {

    public SockServerProcessor(Job j) {
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
    
    private class Task2 extends WorkUnit {
        public Task2(Processor proc) {   super(proc, "task2");        }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
        	
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
			
            return FlowInfo.END;
        }
    }
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {            super(proc, "task1");        }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            TCPEvent ev= (TCPEvent) job.getEvent();
            sockBin(ev);
            return new FlowInfo(10002, 1000, null);
        }
        private void sockBin(TCPEvent ev) throws Exception {        
            InputStream bf= new BufferedInputStream( ev.getSockIn());
            byte[] buf= new byte[4];
            int clen;
            bf.read(buf);
            clen=ByteUte.readAsInt(buf);
            buf= new byte[clen];
            bf.read(buf);
            System.out.println("TCP Server Received: " + new String(buf) ) ;
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
