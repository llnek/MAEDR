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
  
package demo.ssl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;
import com.zotoh.netio.HTTPStatus;




/**
 * @author kenl
 *
 */
class SSLServerFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            def res= new HttpEventResult();
            def ev= job.getEvent();
            
            def date= new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format( new Date() );
            def text="""
            <html>
            <h1>The current date-time is:</h1>
            <h2>Connection:SSL</h2>
            <p>
            ${date}
            </br>
            </p>
            </html>
"""
;
            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
            res.setStatus(HTTPStatus.OK) ;
            
            // associate this result with the orignal event
            // this will trigger the httpresponse
            ev.setResult(res) ;
                    
            println("\nPRESS Ctrl-C anytime to end program.\n");
        }
    };
    
    def SSLServerFlow(Job j) {
        super(j);
    }

    def void preStart() {    		
		println("\n\n-> Point your browser to https://<hostname>:8080");
    }

    def Activity onStart() {
        return new PTask(task1);
    }
    

	public static class Preamble extends MiniWFlow {
		def Preamble(Job j) { super(j); }
		def Activity onStart() {
			return new PTask( new Work() {
				void eval(Job job, Object closure) {
					println("Point your browser to https://" +
						com.zotoh.netio.NetUte.getLocalHost() +
						":8080/test/helloworld"
					);
				}
			});
		}
	}

	    
}


