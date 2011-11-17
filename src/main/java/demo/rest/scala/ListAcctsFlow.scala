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
  

package demo.rest

import _root_.scala.collection.JavaConverters._
import com.zotoh.maedr.device.{RESTEventResult,Event}
import com.zotoh.core.io.StreamData
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._
import com.zotoh.netio.HTTPStatus





/**
 * @author kenl
 *
 */
class ListAcctsFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val res= new RESTEventResult()
            val ev= job.getEvent()
            val text=        <html>
                <h1>The list of accounts are:</h1>
                <p>
                { String.format("Account: %-48sID: %s\n", "Joe Bloggs", "823234") }
                <br/>
                { String.format("Account: %-48sID: %s\n", "Mary Anne", "389423") }
                <br/>
                { String.format("Account: %-48sID: %s\n", "Scott Tiger", "178323") }
                <br/>
                </p>
                </html>.buildString(false)

                // construct a simple html page back to caller
                // by wrapping it into a stream data object
                res.setData(new StreamData( text.getBytes("utf-8") ) )
                res.setStatus(HTTPStatus.OK) 
                                                
            ev.setResult(res)
        }
    }
    
    override def onStart() : Activity = {
        new PTask(task1)
    }

    
}



class ListAcctsFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Point your browser to http://" +
                        com.zotoh.netio.NetUte.getLocalHost() +
                        ":8080/storefront/account/list"
                    );
                    println("Point your browser to http://" +
                                    com.zotoh.netio.NetUte.getLocalHost() +
                                    ":8080/storefront/account/[1-9][0-9]*"
                                );
                    println("Point your browser to http://" +
                                    com.zotoh.netio.NetUte.getLocalHost() +
                                    ":8080/storefront/cart/[1-9][0-9]*"
                                );					              
            }
        });
    }
}


