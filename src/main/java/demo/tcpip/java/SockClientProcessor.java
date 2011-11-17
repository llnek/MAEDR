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

import java.io.OutputStream;
import java.net.Socket;

import com.zotoh.core.util.ByteUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.TcpIO;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.netio.NetUte;


/**
 * @author kenl
 *
 */
public class SockClientProcessor extends Stateless {

    public SockClientProcessor(Job j) {
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
        // wait a bit before we start
        return new FlowInfo(10001, 3000, null);
    }
    
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            // opens a socket and write something back to parent process
            TcpIO tcp= (TcpIO) getEngine().getDeviceManager().getDevice("server");
            String host=tcp.getHost();
            int port= tcp.getPort();
            Socket soc= new Socket( NetUte.getNetAddr(host), port);
            String msg= "Hello World!";
            byte[] bits= msg.getBytes();
            System.out.println("TCP Client: about to send message" + msg ) ;
            try        {
                OutputStream os= soc.getOutputStream();
                os.write(ByteUte.readAsBytes(bits.length));
                os.write(bits);
                os.flush();
            }
            finally {
                NetUte.close(soc);
            }            
            return FlowInfo.END;
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
}

