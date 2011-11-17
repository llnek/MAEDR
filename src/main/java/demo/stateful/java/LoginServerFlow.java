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
  
package demo.stateful.java;

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
public class LoginServerFlow extends MiniWFlow {

    Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {            
            HttpEvent ev= (HttpEvent) job.getEvent();
            StrArr a=ev.getParam("user"); 
            String user= a== null ? null : a.getFirst();
            if (StrUte.isEmpty(user)) { return; }
            MiniWFlow f=getCurStep().getFlow();
            f.retrievePreviousState(ColPicker.KEYID, user);
            WState s= f.getState();
            s.setKey(user);
            JSONObject r= f.getState().getRoot();
            JSONArray arr= r.optJSONArray("dates");
            if (arr==null) {
                r.put("dates", arr=new JSONArray());
            }
            arr.put(new Date().getTime()) ;
        }
    };
    
    Work task2= new Work() {
        public void eval(Job job, Object closure) throws Exception {            
            WState ls= getCurStep().getFlow().getState();
            HttpEventResult r= new HttpEventResult();
            StringBuilder list= new StringBuilder();
            JSONArray arr= ls.getRoot().optJSONArray("dates");
            if (arr != null) for (int i=0; i < arr.length(); ++i) {
                String str= new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ")
                .format(new Date(arr.optLong(i)));
                list.append("<li><i>");
                list.append(str);
                list.append("</i></li>");
            }
            StreamData resp= new StreamData();        
            String bf=         
            "<html>\n"
            +"<h1>" + "Shows user: " + ls.getKey() + "'s login history..." + "</h1>\n"
            +"<p><ul>\n"
            + list
            +"\n"
            +"</ul></p>\n"
            +"</html>\n";
            resp.resetMsgContent( bf.getBytes("utf-8"));            
            // pass response into the result object        
            r.setStatus(HTTPStatus.OK);
            r.setData(resp);
            job.getEvent().setResult(r);
            
            
            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            System.out.println("After restart, When you point your browser again to the server with");
            System.out.println("the same user, you should get the complete history back");
            
        }        
    };
    
    /**/
    public LoginServerFlow(Job j) {
        super(j);
    }

    protected void preStart() {    		
		System.out.println(
	        "\n\n-> Point your browser to http://<hostname>:8080/?user=some-name");
    }

    protected void onEnd() {
        String s= (String)getState().getKey();
        if ( ! StrUte.isEmpty(s)) try { 
            persistState(); 
        } 
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    @Override
    protected Activity onStart() {
        return new PTask(task1).chain( new PTask(task2));
    }

    
    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Point your browser to http://" +
                        com.zotoh.netio.NetUte.getLocalHost() +
                        ":8080/test/helloworld?user=joe"
                    );
                }
            });
        }
    }
    
    
}

