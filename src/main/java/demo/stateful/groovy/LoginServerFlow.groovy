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
  
package demo.stateful;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.StrUte;
import com.zotoh.core.util.StrArr;
import com.zotoh.maedr.core.ColPicker;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.WState;
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
class LoginServerFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            def ev= job.getEvent();
            def a=ev.getParam("user"); 
            def user= a?.getFirst();
            if (StrUte.isEmpty(user)) { return; }
            def f=getCurStep().getFlow();
            f.retrievePreviousState(ColPicker.KEYID, user);
            def ls= f.getState();
            ls.setKey(user);
            def arr= ls.getRoot().optJSONArray("dates");
            if (arr==null) {
                ls.getRoot().put("dates", arr=new JSONArray());
            }
            arr.put(new Date().getTime()) ;
        }
    };
    
    def task2= new Work() {
        void eval(Job job, Object closure) {
            def ls= getCurStep().getFlow().getState();
            def r= new HttpEventResult();
            def list= new StringBuilder();
            def arr=ls.getRoot().optJSONArray("dates");
            if (arr != null) for (int i=0; i < arr.length(); ++i) {
                def str= new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format(new Date(arr.optLong(i)));
                list.append("<li><i>");
                list.append(str);
                list.append("</i></li>");
            }
            def resp= new StreamData();        
            String bf=         """
            <html>
            <h1>Shows user: ${ls.getKey()}'s login history...</h1>
            <p><ul>
            ${list}
            </br>
            </ul></p>
            </html>
""";
            resp.resetMsgContent( bf.getBytes("utf-8"));            
            // pass response into the result object        
            r.setStatus(HTTPStatus.OK);
            r.setData(resp);
            job.getEvent().setResult(r);
            
            
            println("\nPRESS Ctrl-C anytime to end program.\n");
            println("After restart, When you point your browser again to the server with");
            println("the same user, you should get the complete history back");
            
        }        
    };
    
    /**/
    def LoginServerFlow(Job j) {
        super(j);
    }

    void preStart() {    		
		println(
	        "\n\n-> Point your browser to http://<hostname>:8080/?user=some-name");
    }

    void onEnd() {
        def s= getState().getKey();
        if ( ! StrUte.isEmpty(s)) try { 
            persistState(); 
        } 
        catch (e) { 
            e.printStackTrace(); 
        }
    }
    
    
    Activity onStart() {
        return new PTask(task1).chain( new PTask(task2));
    }
    
	
	public static class Preamble extends MiniWFlow {
		def Preamble(Job j) { super(j); }
		def Activity onStart() {
			return new PTask( new Work() {
				void eval(Job job, Object closure) {
					println("Point your browser to http://" +
						com.zotoh.netio.NetUte.getLocalHost() +
						":8080/test/helloworld?user=joe"
					);
				}
			});
		}
	}

	
}



