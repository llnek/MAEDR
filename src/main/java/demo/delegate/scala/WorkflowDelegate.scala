/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
  

package demo.delegate

import com.zotoh.maedr.core.{AppEngine,Job,AppDelegate}
import com.zotoh.maedr.device.{HttpEvent,Event}
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
class WorkflowDelegate(eng:AppEngine[MiniWFlow,FlowStep]) extends AppDelegate[MiniWFlow,FlowStep](eng) {

    println(
                "Point your browser to http://"+
                  com.zotoh.netio.NetUte.getLocalHost() +
                ":8080/test/helloworld")

    override def newProcess(job:Job) : MiniWFlow = {
        
        // this is where you can decide how to react to jobs in a generic way
        // instead of defining processors in the device manifest file.
        
        val ev= job.getEvent();
        
        if (ev.isInstanceOf[HttpEvent]) {
            // all http related events are to be handled by instances of this processor
            new HTTPServerFlow(job)
        } else {
            MiniWFlow.FLOW_NUL
        }
    }

    override def onShutdown() {
        // if you need to do something specific as part of the shutdown cycle...
        println("Bye Bye!")
        super.onShutdown()
    }
    
    
    
    
}
