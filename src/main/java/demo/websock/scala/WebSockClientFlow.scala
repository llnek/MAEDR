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
  

package demo.websock

import _root_.java.net.URI

import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame

import com.zotoh.maedr.device.TimerEvent
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.service.netty._
import com.zotoh.maedr.service._
import com.zotoh.maedr.wflow._

/**
 * @author kenl
 *
 */
class WebSockClientFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
        		if (job.getEvent().isInstanceOf[TimerEvent]) {
        			val c=WebSocketClientFactory.newClient(
					new URI("http://localhost:8080/squarenum"), new WebSocketClientCB() {
						override def onFrame(c:WebSocketClient,
										frame:WebSocketFrame) {
				            println( "Client got Message: result: " + 
							frame.getTextData())
				            println("\nPRESS Ctrl-C anytime to end program.\n")
				            c.stop()
						}
						override def onError(c:WebSocketClient, t:Throwable) {
						}
						override def onDisconnect(c:WebSocketClient ) {
						}
						override def onConnect(c:WebSocketClient ) {
						}						
					})
        			c.start()
	                println( "Client send Message: square this number: 13" )
                    Thread.sleep(3000)
        			c.send(new DefaultWebSocketFrame("13"))
        		}
        	
        }
    };
    
    override def onStart() : Activity = {
        new PTask(task1)
    }
    
    
}
