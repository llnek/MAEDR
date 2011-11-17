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
  
package demo.stateful.java;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;

import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.StrUte;
import com.zotoh.core.util.StrArr;
import com.zotoh.maedr.core.ColPicker;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.WState;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateful;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.netio.HTTPStatus;

/**
 * @author kenl
 *
 */
public class LoginServerProcessor extends Stateful {

    /**/
    public LoginServerProcessor(Job j) {
        super(j);
    }

    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
            case 10002: return new Task2(this);
        }
        return WorkUnit.NONE;
    }

    protected FlowInfo onStart(Event e) {
    		
    		System.out.println("\n\n-> Point your browser to http://localhost:8080/?user=some-name");
    		
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }

    protected void onEnd() {
        String s= (String)getState().getKey();
        if ( ! StrUte.isEmpty(s)) try { persistState(); } catch (Exception e) { e.printStackTrace(); }
    }
    
    private class Task2 extends WorkUnit {

        public Task2(Processor proc) {
            super(proc, "task2");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            HttpEventResult r= new HttpEventResult();
            WState ls= getProcessor().getState();
            StringBuilder list= new StringBuilder();
            JSONArray arr=ls.getRoot().optJSONArray("dates");
            if (arr != null) for (int i =0; i < arr.length(); ++i) {
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
            
            return FlowInfo.END;
        }        
    }
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        @Override
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            HttpEvent ev= (HttpEvent) job.getEvent();
            StrArr a=ev.getParam("user"); 
            String user= a== null ? null : a.getFirst();
            if (StrUte.isEmpty(user)) { return null; }
            Stateful p= (Stateful) getProcessor();
            p.retrievePreviousState(ColPicker.KEYID, user);
            WState ls= p.getState();
            ls.setKey(user);
            JSONArray arr= ls.getRoot().optJSONArray("dates") ;
            if (arr == null) {
                ls.getRoot().put("dates", arr=new JSONArray());
            }
            arr.put(new Date().getTime()) ;
            return new FlowInfo(10002);
        }
        
    }
    
}

