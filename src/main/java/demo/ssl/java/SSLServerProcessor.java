/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
  
package demo.ssl.java;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.netio.HTTPStatus;


/**
 * @author kenl
 *
 */
public class SSLServerProcessor extends Stateless {

    public SSLServerProcessor(Job j) {
        super(j);
    }

    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    protected FlowInfo onStart(Event e) {
    		
    		System.out.println("\n\n-> Point your browser to https://localhost:8080");
    		
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }

    
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            HttpEventResult res= new HttpEventResult();
            HttpEvent ev= (HttpEvent) job.getEvent();
            
            String text=        
            "<html>\n"
            +"<h1>The current date-time is:</h1>\n"
            +"<h2>Connection:SSL</h2>\n"
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
            // this will trigger the httpresponse
            ev.setResult(res) ;
                    
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            
            // done - end.
            return FlowInfo.END;
        }
        
    }
    
}


