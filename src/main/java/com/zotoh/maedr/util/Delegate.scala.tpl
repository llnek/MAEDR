package ${PACKAGE_ID}


import com.zotoh.maedr.core.{Job,AppEngine}
import com.zotoh.maedr.wflow._
import com.zotoh.maedr.device.Event


class ${CLASS_NAME}(eng : FlowBaseEngine) extends WFlowDelegate(eng) {

  override def onShutdown {
        // add specific code to handle shutdown
  }

  override def newProcess(job:Job) : MiniWFlow = {

    val ev= job.getEvent()

    // You decide on how to react to this job by returning a workflow object back to the
    // engine.  How you decide is up to you, it can be simply be based on event type, 
    // or content specific -
    // that is, take a look inside the event and based on its content, determine
    // what workflow to invoke.  Or you may have one giant workflow which takes
    // care of everything!.
    
    new ${PROC_CLASS_NAME}(job)
  }


}

