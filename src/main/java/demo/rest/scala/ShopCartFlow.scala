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

import _root_.java.util.List

import com.zotoh.maedr.device.{RESTEvent,RESTEventResult}
import com.zotoh.maedr.http.{UriPathChain,UriPathElement}
import com.zotoh.core.io.StreamData
import com.zotoh.maedr.core.Job
import com.zotoh.netio.HTTPStatus
import com.zotoh.maedr.wflow._


/**
 * @author kenl
 *
 */
class ShopCartFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[RESTEvent]
            val res= new RESTEventResult()
            
            val c= ev.getPath()
            var p=""
            val lst= c.getElements()
            var pos= -1
            lst.asScala.map{ em : Any =>
                pos=pos+1
                if ( "/cart".equals(em.asInstanceOf[UriPathElement].getPath())) {
                    p=lst.get(pos+1).getPath();
                }
            }
            p=p.replaceAll("^/", "")
            val text=        <html>
                <h1>The shopping cart id received:</h1>
                <p>
                { p }
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
