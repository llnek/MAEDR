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
  
package demo.tcpip

import _root_.java.io.OutputStream
import _root_.java.net.Socket

import com.zotoh.maedr.device.TcpIO
import com.zotoh.core.util.ByteUte
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._
import com.zotoh.netio.NetUte


/**
 * @author kenl
 *
 */
class SockClientFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            // opens a socket and write something back to parent process
            val tcp= getEngine().getDeviceManager().getDevice("server").asInstanceOf[TcpIO]
            val host=tcp.getHost()
            val port= tcp.getPort()
            val soc= new Socket( NetUte.getNetAddr(host), port)
            val msg= "Hello World!"
            val bits= msg.getBytes()
            println("TCP Client: about to send message" + msg ) 
            try        {
                val os= soc.getOutputStream()
                os.write(ByteUte.readAsBytes(bits.length))
                os.write(bits)
                os.flush()
            }
            finally {
                NetUte.close(soc)
            }                        
        }
    }
    
    override def onStart() : Activity = {
        new Delay(3000).chain(new PTask(task1))
    }
    
    
}

