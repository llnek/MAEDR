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
 

package com.zotoh.maedr.wflow;

import static com.zotoh.core.util.StrUte.nsb;

import com.zotoh.core.util.StrArr;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.netio.HTTPStatus;

/**
 * Handles internal system events.
 * (Internal use only)
 *
 * @author kenl
 */
class BuiltinFlow extends MiniWFlow {

    /**
     * @param j
     */
    public BuiltinFlow(Job j) {
        super(j);
    }

	@Override
	protected Activity onStart() {
		final BuiltinFlow me=this;
		
		Activity t1,t2,t3= new PTask().withWork(new Work() {
			public void eval(Job job, Object arg) throws Exception {
				me.eval_shutdown(job);
			}			
		});
        t2= new Delay(3000L);
        t1= new PTask().withWork(new Work() {
            public void eval(Job job, Object arg) throws Exception {
                me.do_shutdown(job);
            }           
        });
		
		
		BoolExpr t= new BoolExpr(){
			public boolean eval(Job job) {
		        String id= job.getEvent().getDevice().getId();
		        return SHUTDOWN_DEVID.equals(id);
			}};

		return new If(t, t3.chain(t2).chain(t1));
		
	}

    private void do_shutdown(Job job) throws Exception {
        job.getEngine().shutdown();        
    }
    
    private void eval_shutdown(Job job) throws Exception {
        HttpEventResult res= new HttpEventResult();
        HttpEvent ev= (HttpEvent) job.getEvent();
        StrArr a=ev.getParam("pwd");
        boolean ignore=false;
        String w= "";
        
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
            
        }
        
    }
	
}
