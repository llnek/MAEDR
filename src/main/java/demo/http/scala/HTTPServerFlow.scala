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
  

package demo.http

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date

import com.zotoh.maedr.device.{HttpEventResult,HttpEvent}
import com.zotoh.core.io.StreamData
import com.zotoh.maedr.core.Job
import com.zotoh.netio.HTTPStatus
import com.zotoh.maedr.wflow._

/**
 * @author kenl
 *
 */
class HTTPServerFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[HttpEvent]
            val res= new HttpEventResult();
            val text= <html>
            <h1>The current date-time is:</h1>
            <p>
              { new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format( new Date() ) }
            </p>
            </html>.buildString(false)

            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) )
            res.setStatus(HTTPStatus.OK)
            
            // associate this result with the orignal event
            // this will trigger the http response
            ev.setResult(res) 
                    
            println("\nPRESS Ctrl-C anytime to end program.\n")
            
        }
    }
    
    override def onStart() : Activity = {
        new PTask( task1)
    }
    
}


class HTTPServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                println("Point your browser to http://" +
                    com.zotoh.netio.NetUte.getLocalHost() +
                    ":8080/test/helloworld"
                )
            }
        })
    }
}

