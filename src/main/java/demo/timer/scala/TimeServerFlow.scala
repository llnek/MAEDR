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
  
package demo.timer

import _root_.java.util.Date

import com.zotoh.maedr.core.Job
import com.zotoh.maedr.device.TimerEvent
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
object TimeServerFlow {
  private var _count=0
  def upCount() : Int = { _count = _count+1 ; _count }
  def count() : Int = { _count }
}
class TimeServerFlow(job:Job) extends MiniWFlow(job) {
    
    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[TimerEvent]
            if ( ev.isRepeating()) {
                if (TimeServerFlow.count() < 5) {
                    println("-----> repeating-update: " + new Date()) 
                    TimeServerFlow.upCount()
                }
                if (TimeServerFlow.upCount() >= 5) {
                        println("\nPRESS Ctrl-C anytime to end program.\n")
                }
            }
            else {
                println("-----> once-only!!: " + new Date()) 
            }
        }
    }
    
    override def onStart() : Activity = {
        new PTask(task1)
    }





}

class TimeServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo various timer functions..." )
            }
        })
    }
}



