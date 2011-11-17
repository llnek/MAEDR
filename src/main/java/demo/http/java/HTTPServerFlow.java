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
  

package demo.http.java;

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
public class HTTPServerFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
            
            HttpEventResult res= new HttpEventResult();
            HttpEvent ev= (HttpEvent) job.getEvent();            
            String text=        
            "<html>\n"
            +"<h1>The current date-time is:</h1>\n"
            +"<p>\n"
            + new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format( new Date() )
            +"\n"
            +"</p>\n"
            +"</html>\n";

            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
            res.setStatus(HTTPStatus.OK) ;
            
            // associate this result with the orignal event
            // this will trigger the http response
            ev.setResult(res) ;
                    
            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            
        }
    };
    
	/**/
    public HTTPServerFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new PTask( task1);
    }
    
    
    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Point your browser to http://" +
                        com.zotoh.netio.NetUte.getLocalHost() +
                        ":8080/test/helloworld"
                    );
                }
            });
        }
    }
    
}
