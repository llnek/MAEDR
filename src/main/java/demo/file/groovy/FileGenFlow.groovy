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
  
package demo.file;

import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.fmtDate;

import java.io.File;
import java.util.Date;

import com.zotoh.core.util.GUID;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.FilePicker;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 * Create a new file every n secs
 *
 */
class FileGenFlow extends MiniWFlow {

    def task1= new Work() {
        void eval(Job job, Object closure) {
            
            def p= job.getEngine().getDeviceManager().getDevice("picker");
            def out= new File(p.getSrcDir(), GUID.generate()+".txt");
            def s= "Current time is " + fmtDate(new Date());
            writeFile(out, s, "utf-8");
        }
        
    } ;
    
    def FileGenFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask( task1);
    }
    


    public static class Preamble extends MiniWFlow {
        def Preamble(Job j) { super(j); }
        def Activity onStart() {
            return new PTask( new Work() {
                void eval(Job job, Object closure) {
                    println("Demo file directory monitoring - picking up new files");                    
                }
            });
        }
    }

}


