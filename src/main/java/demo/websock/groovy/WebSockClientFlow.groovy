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
  

package demo.websock;

import java.net.URI;

import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.TimerEvent;
import com.zotoh.maedr.service.netty.*;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 *
 */
class WebSockClientFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {

        		if (job.getEvent() instanceof TimerEvent) {
        			def c=WebSocketClientFactory.newClient(
					new URI("http://localhost:8080/squarenum"), new WebSocketClientCB() {
						void onFrame(WebSocketClient c,
										WebSocketFrame frame) {
				            println( "Client got Message: result: " + 
							frame.getTextData());
				            println("\nPRESS Ctrl-C anytime to end program.\n");       
				            c.stop();
						}
						void onError(WebSocketClient c, Throwable t) {
						}
						void onDisconnect(WebSocketClient c) {
						}
						void onConnect(WebSocketClient c) {
						}						
					});
        			c.start();
	                println( "Client send Message: square this number: 13" );
                    Thread.sleep(3000);
        			c.send(new DefaultWebSocketFrame("13"));
        		}
        	
        }
    };
    
    def WebSockClientFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask(task1);
    }
    
    
}
