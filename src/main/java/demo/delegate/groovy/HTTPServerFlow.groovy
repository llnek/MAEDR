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
  

package demo.delegate;

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
class HTTPServerFlow extends MiniWFlow {

    def task1= new Work(){
        void eval (Job job, Object closure) {
            
            def res= new HttpEventResult();
            def ev= job.getEvent();            
            def text=         """
            <html>
            <h1>The current date-time is:</h1>
            <p>
            ${ new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format( new Date() ) }
            </br>
            </p>
            </html>
""" ;
            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
            res.setStatus(HTTPStatus.OK) ;
            
            // associate this result with the orignal event
            // this will trigger the http response
            ev.setResult(res) ;
                    
            println("\nPRESS Ctrl-C anytime to end program.\n");
            
        }
    };
    
	/**/
    def HTTPServerFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask(task1);
    }

    
}
