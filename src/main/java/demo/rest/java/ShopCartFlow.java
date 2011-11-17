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

import java.util.List;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.RESTEvent;
import com.zotoh.maedr.device.RESTEventResult;
import com.zotoh.maedr.http.UriPathChain;
import com.zotoh.maedr.http.UriPathElement;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;
import com.zotoh.netio.HTTPStatus;


/**
 * @author kenl
 *
 */
public class ShopCartFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
            RESTEventResult res= new RESTEventResult();
            RESTEvent ev= (RESTEvent) job.getEvent();
            
            UriPathChain c= ev.getPath();
            String p="";
            List<UriPathElement> lst= c.getElements();
            
            for (int i=0; i < lst.size(); ++i) {
                if ( "/cart".equals(lst.get(i).getPath())) {
                    p=lst.get(i+1).getPath();
                    break;
                }
            }                   
            p=p.replaceAll("^/", "");
            
            String text=        
                "<html>\n"
                +"<h1>The shopping cart id received:</h1>\n"
                +"<p>\n"
                + p
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
    public ShopCartFlow(Job j) {
        super(j);
    }
    
    @Override
    protected Activity onStart() {
        return new PTask(task1);
    }

    
    
}
