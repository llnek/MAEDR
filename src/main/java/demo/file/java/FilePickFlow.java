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
  
package demo.file.java;

import java.io.File;

import com.zotoh.core.io.StreamUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.FileEvent;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 *
 */
public class FilePickFlow extends MiniWFlow {

	private static int _count=0;
	Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {           
            FileEvent ev= (FileEvent) job.getEvent();
            String f0= ev.getOrigFilePath();
            File f=ev.getFile();
            System.out.println("Found new file: " + f0);
            System.out.println("Content: " + StreamUte.readFile(f, "utf-8"));
            
            ++_count;
            
            if (_count > 3) {
                System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            }
        }              
    };
	
	
    public FilePickFlow(Job j) {
        super(j);
    }
    
    @Override
    protected Activity onStart() {
        return new PTask( task1);
    }
    
}


