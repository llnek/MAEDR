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
  

package demo.rest.java;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.RESTEventResult;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;
import com.zotoh.netio.HTTPStatus;


/**
 * @author kenl
 *
 */
public class ListAcctsFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
            RESTEventResult res= new RESTEventResult();
            Event ev= job.getEvent();
            
            String text=        
                "<html>\n"
                +"<h1>The list of accounts are:</h1>\n"
                +"<p>\n"
                + String.format("Account: %-48sID: %s\n", "Joe Bloggs", "823234")
                +"<br/>"
                + String.format("Account: %-48sID: %s\n", "Mary Anne", "389423")
                +"<br/>"
                + String.format("Account: %-48sID: %s\n", "Scott Tiger", "178323")
                +"<br/>"
                +"</p>\n"
                +"</html>\n";

                // construct a simple html page back to caller
                // by wrapping it into a stream data object
                res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
                res.setStatus(HTTPStatus.OK) ;
                                                
            ev.setResult(res);
        }
    };
    
	/**/
    public ListAcctsFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new PTask(task1);
    }

    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Point your browser to http://" +
                        com.zotoh.netio.NetUte.getLocalHost() +
                        ":8080/storefront/account/list"
                    );
                    System.out.println("Point your browser to http://" +
                                    com.zotoh.netio.NetUte.getLocalHost() +
                                    ":8080/storefront/account/[1-9][0-9]*"
                                );
                    System.out.println("Point your browser to http://" +
                                    com.zotoh.netio.NetUte.getLocalHost() +
                                    ":8080/storefront/cart/[1-9][0-9]*"
                                );
                }
            });
        }
    }
    
    
    
}
