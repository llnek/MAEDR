package ${PACKAGE_ID}

import com.zotoh.maedr.process.{Processor, WorkUnit}
import com.zotoh.maedr.core.{Job, JobData, FlowInfo}
import com.zotoh.maedr.device.Event


class ${CLASS_NAME}(proc:Processor) extends WorkUnit(proc,"${TASK_ID}") {

  override def evalOneStep(job : Job, closureArg : Object) : FlowInfo = {

        // with the job-data object, you can store transient data which
        // can be shared amongst tasks within this job.
        // if you need to store persistent data, put it inside your
        // process state object.
        val data= job.getData()
        // the event that triggered this job.
        val ev= job.getEvent()

        // do your stuff here

        // return a new flow indicator to proceed to next step
        // or null to indicate end of processing of the job.
        FlowInfo.END
        // or
        //new FlowInfo(10002)
  }

}

