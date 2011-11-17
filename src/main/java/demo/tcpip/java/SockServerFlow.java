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
  
package demo.tcpip.java;

import java.io.BufferedInputStream;
import java.io.InputStream;

import com.zotoh.core.util.ByteUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.TCPEvent;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.Delay;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


/**
 * @author kenl
 *
 */
public class SockServerFlow extends MiniWFlow {

    Work task1= new Work() {
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
        public void eval(Job job, Object closure) throws Exception {
            TCPEvent ev= (TCPEvent) job.getEvent();
            sockBin(ev);
            // add a delay into the workflow before next step
            setResult( new Delay(1500));            
        }
    };

    Work task2= new Work() {
        public void eval(Job job, Object closure) {
            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");            
        }
    };
    
    public SockServerFlow(Job j) {
        super(j);
    }
    
    @Override
    protected Activity onStart() {
        return new PTask(task1).chain(new PTask(task2));
    }
    
    
    
    
    
    
    
    
    
    
    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Demo sending & receiving messages via tcpip..." );
                }
            });
        }
    }
    
    
    
    
}
