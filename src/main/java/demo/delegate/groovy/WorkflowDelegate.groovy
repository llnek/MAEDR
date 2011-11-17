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
  

package demo.delegate;

import com.zotoh.maedr.core.AppDelegate;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.wflow.FlowStep;
import com.zotoh.maedr.wflow.MiniWFlow;
import static com.zotoh.maedr.wflow.MiniWFlow.*;


/**
 * @author kenl
 *
 */
class WorkflowDelegate extends AppDelegate<MiniWFlow,FlowStep> {

    def WorkflowDelegate(AppEngine<MiniWFlow,FlowStep> eng) {
        super(eng);
        def host=com.zotoh.netio.NetUte.getLocalHost();
        println(
                "Point your browser to http://"+host+":8080/test/helloworld");
    }

    def MiniWFlow newProcess(Job job) {
        
        // this is where you can decide how to react to jobs in a generic way
        // instead of defining processors in the device manifest file.
        
        def ev= job.getEvent();
        
        if (ev instanceof HttpEvent) {
            // all http related events are to be handled by instances of this processor
            return new HTTPServerFlow(job);
        }
                
        return FLOW_NUL;
    }

    def void onShutdown() {
        // if you need to do something specific as part of the shutdown cycle...
        println("Bye Bye!");
        super.onShutdown();
    }
    
    
    
    
}
