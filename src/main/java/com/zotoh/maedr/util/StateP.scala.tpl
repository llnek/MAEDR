package ${PACKAGE_ID}

import com.zotoh.maedr.device.Event
import com.zotoh.maedr.core.{Job, JobData}
import com.zotoh.maedr.wflow._


class ${CLASS_NAME}(job : Job) extends  MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job : Job, closureArg : Object) : Unit = {
                    val ev= job.getEvent()
                    // do your stuff here
                    println("hello world")                                           
                    
        }
    }
    
  override def onStart : Activity = {
    new PTask( task1 )
  }




}

