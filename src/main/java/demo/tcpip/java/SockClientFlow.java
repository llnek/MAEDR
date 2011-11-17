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

import java.io.OutputStream;
import java.net.Socket;

import com.zotoh.core.util.ByteUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.TcpIO;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.Delay;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;
import com.zotoh.netio.NetUte;


/**
 * @author kenl
 *
 */
public class SockClientFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
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
        }
    };
    
    public SockClientFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new Delay(3000).chain(new PTask(task1));
    }
    
    
}

