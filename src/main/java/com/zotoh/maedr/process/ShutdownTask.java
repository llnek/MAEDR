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


package com.zotoh.maedr.process;

import static com.zotoh.core.util.StrUte.nsb;

import com.zotoh.core.util.StrArr;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.netio.HTTPStatus;

/**
 * (Internal use only)
 *
 * @author kenl
 */
class ShutdownTask extends WorkUnit implements Vars {

    /**
     * @param proc
     */
    public ShutdownTask(Processor proc) {
        super(proc, "shutdown-task");
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.process.WorkUnit#evalOneStep(com.zotoh.maedr.core.Job, java.lang.Object)
     */
    @Override
    protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
        return (closureArg==null) ? eval_0(job) : eval_1(job);
    }
    
    private FlowInfo eval_1(Job job) throws Exception {
        job.getEngine().shutdown();
        return FlowInfo.END;
    }
    
    private FlowInfo eval_0(Job job) throws Exception {
        HttpEventResult res= new HttpEventResult();
        HttpEvent ev= (HttpEvent) job.getEvent();
        StrArr a=ev.getParam("pwd");
        boolean ignore=false;
        String w= "";
        FlowInfo fi=null;
        
        if (a==null) {
            a= ev.getParam("password");
        }
        if (a != null) { w=nsb(a.getFirst()); }
                
        if ( ! job.getEngine().verifyShutdown(ev.getUri(), w)) { ignore=true; }
        
        if (ignore) {
            tlog().warn("ShutdownTask: wrong password or uri, ignore shutdown request");
            res.setStatus(HTTPStatus.FORBIDDEN);
        }
        else {
            res.setStatus(HTTPStatus.OK);
        }
        ev.setResult(res);
        
        if (!ignore) {
            fi= new FlowInfo(10002, 3000L, "byebye");            
        }
        
        return fi;
    }

}
