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
  

package demo.rest;

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
class ShopCartFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            def res= new RESTEventResult();
            def ev= job.getEvent();
            
            def c= ev.getPath();
            def p="";
            def lst= c.getElements();
            
            for (int i=0; i < lst.size(); ++i) {
                if ( "/cart".equals(lst.get(i).getPath())) {
                    p=lst.get(i+1).getPath();
                    break;
                }
            }                   
            p=p.replaceAll("^/", "");
            
            def text="""
            <html>
                <h1>The shopping cart id received:</h1>
                <p>
                 ${ p }
                <br/>
                </p>
                </html>
""" ;
                // construct a simple html page back to caller
                // by wrapping it into a stream data object
                res.setData(new StreamData( text.getBytes("utf-8") ) ) ;
                res.setStatus(HTTPStatus.OK) ;
            
            ev.setResult(res);
        }
    };
    
	/**/
    def ShopCartFlow(Job j) {
        super(j);
    }
    
    def Activity onStart() {
        return new PTask(task1);
    }

    
    
}
