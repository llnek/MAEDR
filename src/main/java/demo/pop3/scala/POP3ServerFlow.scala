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
  
package demo.pop3

import com.zotoh.maedr.device.POP3Event
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
object POP3ServerFlow {
  private var _count=0
  def upCount() : Unit ={ _count=_count+1 }
  def count() : Int ={ _count }
}
class POP3ServerFlow(job:Job) extends MiniWFlow(job) {

	val task1= new Work() {
	    override def eval(job:Job, closure:Object) {
            val e= job.getEvent().asInstanceOf[POP3Event]
            val bits=e.getMsg().getBytes()
            println("########################")
            print(e.getSubject() + "\r\n")
            print(e.getFrom() + "\r\n")
            print(e.getTo() + "\r\n")
            print("\r\n")
            println(new String(bits,"utf-8"))
            
            POP3ServerFlow.upCount()
            
            if (POP3ServerFlow.count() > 3) {
                    println("\nPRESS Ctrl-C anytime to end program.\n")
            }
	    }
	}
	
    override def onStart() : Activity = {
        new PTask(task1)
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

class POP3ServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo receiving POP3 emails..." )
            }
        })
    }
}


