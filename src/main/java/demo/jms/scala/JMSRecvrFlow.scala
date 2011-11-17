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
  
package demo.jms

import javax.jms.{TextMessage,Message}

import com.zotoh.maedr.core.Job
import com.zotoh.maedr.device.JmsEvent
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
object JMSRecvrFlow {
  private var _count=0
  def upCount() : Unit = {
    _count=_count+1
  }
  def count() : Int = { _count }

}
class JMSRecvrFlow(job:Job) extends MiniWFlow(job) {

	val task1= new Work() {
	    override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[JmsEvent]
            val msg= ev.getMsg()

            println("-> Correlation ID= " + msg.getJMSCorrelationID())
            println("-> Msg ID= " + msg.getJMSMessageID())
            println("-> Type= " + msg.getJMSType())
            
            if (msg.isInstanceOf[TextMessage]) {
                val t= msg.asInstanceOf[TextMessage]
                println("-> Text Message= " + t.getText())
            }
            
            JMSRecvrFlow.upCount()
            
            if (JMSRecvrFlow.count() > 3) {
                    println("\nPRESS Ctrl-C anytime to end program.\n")
            }
            	        
	    }
	}
	
    override def onStart() : Activity = {
        new PTask(task1)
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

class JMSRecvrFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo receiving JMS messages..." )
            }
        })
    }
}


