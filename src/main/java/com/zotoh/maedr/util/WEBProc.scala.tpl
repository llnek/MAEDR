package ${PACKAGE_ID}


import com.zotoh.maedr.core.Job
import com.zotoh.maedr.device.{ ServletEvent, ServletEventResult } 
import com.zotoh.maedr.wflow._


class WEBProcessor (job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job : Job, closureArg : Object) : Unit = {
                val res= new ServletEventResult();
                val ev= job.getEvent();                    
                
                res.setData("<html><body>Bonjour!</body></html>");
                ev.setResult(res) ;
        
        }
    }
    
          override def onStart : Activity = {
                new PTask(task1)
        }



        
    
}
