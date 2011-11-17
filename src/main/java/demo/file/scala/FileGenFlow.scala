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
  
package demo.file

import _root_.java.util.Date
import _root_.java.io.File

import com.zotoh.core.io.StreamUte.writeFile
import com.zotoh.core.util.CoreUte.fmtDate
import com.zotoh.core.util.GUID
import com.zotoh.maedr.device.FilePicker
import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._

/**
 * @author kenl
 * Create a new file every n secs
 *
 */
class FileGenFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) : Unit = {
            val p= job.getEngine().getDeviceManager().getDevice("picker").asInstanceOf[FilePicker]
            val out= new File(p.getSrcDir(), GUID.generate()+".txt")
            val s= "Current time is " + fmtDate(new Date())
            writeFile(out, s, "utf-8")
        }
    }
    
    override def onStart() : Activity = {
        new PTask( task1)
    }
    
}

class FileGenFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo file directory monitoring - picking up new files")
            }
        })
    }
}


