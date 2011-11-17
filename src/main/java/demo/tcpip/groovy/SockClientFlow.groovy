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
  
package demo.tcpip;

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
class SockClientFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            // opens a socket and write something back to parent process
            def tcp= getEngine().getDeviceManager().getDevice("server");
            def host=tcp.getHost();
            def port= tcp.getPort();
            def soc= new Socket( NetUte.getNetAddr(host), port);
            def msg= "Hello World!";
            def bits= msg.getBytes();
            println("TCP Client: about to send message" + msg ) ;
            try        {
                def os= soc.getOutputStream();
                os.write(ByteUte.readAsBytes(bits.length));
                os.write(bits);
                os.flush();
            }
            finally {
                NetUte.close(soc);
            }                        
        }
    };
    
    def SockClientFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new Delay(3000).chain(new PTask(task1));
    }
    
    
}

