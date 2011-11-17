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
  

package demo.jetty;


import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.ServletEvent;
import com.zotoh.maedr.device.ServletEventResult;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;
import com.zotoh.netio.HTTPStatus;


/**
 * @author kenl
 *
 */
class JettyServerFlow extends MiniWFlow {

    def task1= new Work() {
        
        void eval(Job job, Object closure) {

            def fmtHtml= { ->
                return """
                <html><head>
                <title>MAEDR: Test Embedded Jetty</title>
                <link rel="shortcut icon" href="images/favicon.ico"/>
                <link type="text/css" rel="stylesheet" href="styles/main.css"/>
                <script type="text/javascript" src="scripts/test.js"></script>
                </head><body><h1>Bonjour!</h1><br/><button type="button" onclick="pop();">Click Me!</button></body></html>
""" ;
            }
            
            def res= new ServletEventResult();
            def ev= job.getEvent();            
            //ev.getReq();
            def text= fmtHtml();

            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
            res.setStatus(HTTPStatus.OK) ;
            
            // associate this result with the orignal event
            // trigger the http response
            ev.setResult(res) ;
                    
            println("\nPRESS Ctrl-C anytime to end program.\n");
            
            
        }
    };

    
	/**/
    def JettyServerFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask( task1);
    }
    

	
	
	public static class Preamble extends MiniWFlow {
		def Preamble(Job j) { super(j); }
		def Activity onStart() {
			return new PTask( new Work() {
				void eval(Job job, Object closure) {
					println("Point your browser to http://" +
						com.zotoh.netio.NetUte.getLocalHost() +
						":8080/test/helloworld"
					);
				}
			});
		}
	}

}
