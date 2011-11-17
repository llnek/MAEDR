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
  

package demo.websock.java;

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
public class WebSockClientFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {        

        		if (job.getEvent() instanceof TimerEvent) {
        			WebSocketClient c=WebSocketClientFactory.newClient(
					new URI("http://localhost:8080/squarenum"), new WebSocketClientCB() {
						public void onFrame(WebSocketClient c,
										WebSocketFrame frame) {
				            System.out.println( "Client got Message: result: " + 
							frame.getTextData());
				            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");       
				            c.stop();
						}
						public void onError(WebSocketClient c, Throwable t) {
						}
						public void onDisconnect(WebSocketClient c) {
						}
						public void onConnect(WebSocketClient c) {
						}						
					});
        			c.start();
	            System.out.println( "Client send Message: square this number: 13" );
                    Thread.sleep(3000);
        			c.send(new DefaultWebSocketFrame("13"));
        		}
        	
        }
    };
    
    public WebSockClientFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new PTask(task1);
    }
    
    
}
