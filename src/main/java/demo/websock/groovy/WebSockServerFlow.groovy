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
  

package demo.websock;

import com.zotoh.core.util.CoreUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.netty.WebSockEvent;
import com.zotoh.maedr.device.WebSockResult;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 *
 */
class WebSockServerFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            def ev= job.getEvent();
            def msg= ev.getText();
            def i= CoreUte.asInt(msg, 0);
            i=i*i;
            msg= Integer.toString(i) ;
            def res= new WebSockResult();
            res.setData(msg);
            ev.setResult(res);
        }
    };
    
    def WebSockServerFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask(task1);
    }
    
    public static class Preamble extends MiniWFlow {
        def Preamble(Job j) { super(j); }
        def Activity onStart() {
            return new PTask( new Work() {
                void eval(Job job, Object closure) {
                    println("Demo websocket protocol..." );
                }
            });
        }
    }
    
}
