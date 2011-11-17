package ${PACKAGE_ID};

import com.zotoh.maedr.core.FlowInfo;
import com.zotoh.maedr.core.JobData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.maedr.device.Event;

class ${CLASS_NAME} extends WorkUnit {

    def ${CLASS_NAME}(Processor p) {
        super(p, "${TASK_ID}");
    }

    protected FlowInfo evalOneStep(Job job, Object closureArg) 
    throws Exception {
        // with the job-data object, you can store transient data which
        // can be shared amongst tasks within this job.
        // if you need to store persistent data, put it inside your
        // process state object.
        def data= job.getData();
        // the event that triggered this job.
        def ev= job.getEvent();

        // do your stuff here

        // return a new flow indicator to proceed to next step
        // or null to indicate end of processing of the job.
        return FlowInfo.END;
        // or
        //return new FlowInfo(10002); 
    }

}

