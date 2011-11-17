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
  
package demo.fork.java;

import java.util.Properties;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class GChildProcessor extends Stateless {
    
    private Object _res, _lhs, _rhs;
    
    public GChildProcessor(Job j) {
        super(j);
    }
    
    protected void setResult(Object o) {
        _res=o;
    }
    
    @Override
    protected WorkUnit getNextWorkUnit() {
        
        switch (getCurStepPos()) {
        
            case 10001: return new Task1(this);
            case 10002: return new Task2(this);
        }
        
        return WorkUnit.NONE;
    }

    /* receive input-data from parent */
    protected void onReceiveDataLink(Properties p)    {
        _lhs=p.get("lhs");
        _rhs=p.get("rhs");
    }
    
    /* output result data back to parent */
    protected void onFinalGiveback(Properties p)    {
        p.put("result", _res);
    }
    
    @Override
    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }

    private class Task1 extends WorkUnit {
        public Task1(Processor proc) {        super(proc, "task1");    }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            System.out.println("Grand-child: Got a request from my parent to do some multiplications.");
            return new FlowInfo(10002);
        }    
    }
    private class Task2 extends WorkUnit {
        public Task2(Processor proc) {        super(proc, "task2");    }
        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            System.out.println("Grand-child: taking some time to do this task... (6secs)");
            for (int i=0; i < 6; ++i) {
            		Thread.sleep(1000);
                System.out.print("...");
            	}
            System.out.println("");
            GChildProcessor p= (GChildProcessor) getProcessor();
            p.setResult( (Integer)_lhs * (Integer)_rhs);
            return FlowInfo.END;
        }    
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}




