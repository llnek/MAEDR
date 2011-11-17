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
  

package demo.jetty.java;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.ServletEvent;
import com.zotoh.maedr.device.ServletEventResult;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.netio.HTTPStatus;

/**
 * @author kenl
 *
 */
public class JettyServerProcessor extends Stateless {

	/**/
    public JettyServerProcessor(Job j) {
        super(j);
    }

	/**/
    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

	/**/
    protected FlowInfo onStart(Event e) {
    	
    		System.out.println("\n\n-> Point your browser to http://localhost:8080/test/helloworld");
    		
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }
    
    
    
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected String fmtHtml() throws Exception {
            return
            "<html><head>" + 
            "<title>MAEDR: Test Embedded Jetty</title>" +
            "<link rel=\"shortcut icon\" href=\"images/favicon.ico\"/>" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"styles/main.css\"/>" +
            "<script type=\"text/javascript\" src=\"scripts/test.js\"></script>" +
            "</head><body><h1>Bonjour!</h1><br/><button type=\"button\" onclick=\"pop();\">Click Me!</button></body></html>";
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            ServletEventResult res= new ServletEventResult();
            ServletEvent ev= (ServletEvent) job.getEvent();            
            //ev.getReq();
            String text= fmtHtml();

            // construct a simple html page back to caller
            // by wrapping it into a stream data object
            res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
            res.setStatus(HTTPStatus.OK) ;
            
            // associate this result with the orignal event
            // trigger the http response
            ev.setResult(res) ;
                    
			System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            
            // done - end.
            return FlowInfo.END;
        }
        
    }
    
    
}
