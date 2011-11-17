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

import _root_.java.io.BufferedInputStream
import _root_.java.io.InputStream

import com.zotoh.core.util.ByteUte
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.device.TCPEvent
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
class SockServerFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[TCPEvent]
            def sockBin(ev:TCPEvent) : Unit = {
              val bf= new BufferedInputStream( ev.getSockIn())
              var buf= new Array[Byte](4);
              var clen=0
              bf.read(buf)
              clen=ByteUte.readAsInt(buf)
              buf= new Array[Byte](clen)
              bf.read(buf)
              println("TCP Server Received: " + new String(buf) )
            }
            sockBin(ev)
            // add a delay into the workflow before next step
            setResult( new Delay(1500))
        }
    }

    val task2= new Work() {
        override def eval(job:Job, closure:Object) {
            println("\nPRESS Ctrl-C anytime to end program.\n")
        }
    }
    
    override def onStart() : Activity = {
        new PTask(task1).chain(new PTask(task2))
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

class SockServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo sending & receiving messages via tcpip..." )
            }
        })
    }
}


