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

package com.zotoh.maedr.test.flow;


import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.junit.Test;

import com.zotoh.core.util.ProcessUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.netty.WebSockEvent;
import com.zotoh.maedr.service.netty.*;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**/
public class JUTWebSock extends BaseJUT {
    
	protected static Object OUT;
	
    /**/
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUTWebSock.class);
    }

    static Work w1= new Work() {
    		public void eval(Job job, Object c) {
    			WebSockEvent e= (WebSockEvent) job.getEvent();
    			job.setData("res", e.getText());
    		}
    };
    
    static Work w2= new Work() {
		public void eval(Job job, Object c) {
		}
    };

    static Work end= new Work() {
		public void eval(Job job, Object c) {
			JUTWebSock b= (JUTWebSock)getCurStep().getFlow().getEngineProperties().get("_");
			OUT=job.getData("res");
			b._eng.shutdown();		
		}
    };
    
    public static class Test1 extends MiniWFlow { 
    		public Test1(Job j) {super(j); }
	    protected Activity onStart() {
	        return new PTask(w1).chain(new PTask(end));
	    }
    }
    
    protected void runClient() throws Exception {
    		WebSocketClient c=
    		WebSocketClientFactory.newClient(new URI("http://localhost:8080/pingpong"), new WebSocketClientCB() {
				public void onFrame(WebSocketClient c, WebSocketFrame frame) {
				}
				public void onError(WebSocketClient c, Throwable t) {
				}
				public void onDisconnect(WebSocketClient c) {
				}
				public void onConnect(WebSocketClient c) {
				}
    		});
    		c.start();
    		ProcessUte.safeThreadWait(3000);
    		c.send(new DefaultWebSocketFrame("helloworld"));
    		ProcessUte.safeThreadWait(3000);

    		c.stop();
    }
    
    //@Test
    public void test1() throws Exception {
    		Properties props=create_props("com.zotoh.maedr.test.flow.JUTWebSock$Test1");
    		final JUTWebSock me=this;
    		OUT=null;
    		props.put("_", this);
    		ProcessUte.asyncExec(new Runnable() {
    			public void run() {
    				try { me.runClient(); } catch (Exception t) { t.printStackTrace(); }
    			}
    		});
    		_eng.start(props);
    		ProcessUte.safeThreadWait(3000);
    		System.out.println("OUT = " + OUT);
		assertEquals(OUT, "helloworld");
    }
    
    protected String deviceBlock(String flow) {
        return "{ devices : {"
                        + "h1 : {"
                        + "processor:\"" + flow + "\","
                        + "type:\"websocket\","
                        + "host:\"localhost\","
                        + "port:8080,"
                        + "uri:\"/pingpong\""
                + "}}}";    	
    }
    
    @Test
    public void testDummy() throws Exception {}
  
}
