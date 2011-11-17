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
  
package demo.file.java;

import java.io.File;

import com.zotoh.core.io.StreamUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.FileEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 *
 */
public class FilePickProcessor extends Stateless {

	private static int _count=0;
	
    public FilePickProcessor(Job j) {
        super(j);
    }

    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }

    
    
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
            FileEvent ev= (FileEvent) job.getEvent();
            String f0= ev.getOrigFilePath();
            File f=ev.getFile();
            System.out.println("New file: " + f0);
            System.out.println("Content: " + StreamUte.readFile(f, "utf-8"));
            
            ++_count;
            
            if (_count > 3) {
				System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            }
            
            return FlowInfo.END;
        }
        
    }
    
}


