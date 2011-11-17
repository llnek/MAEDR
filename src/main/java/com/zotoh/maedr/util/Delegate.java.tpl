package ${PACKAGE_ID};

import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.*;
import com.zotoh.maedr.device.Event;

public class ${CLASS_NAME} extends WFlowDelegate {

    @Override
    public MiniWFlow newProcess(Job job) {
        Event ev= job.getEvent();
        
    // You decide on how to react to this job by returning a workflow object back to the
    // engine.  How you decide is up to you, it can be simply be based on event type, 
    // or content specific -
    // that is, take a look inside the event and based on its content, determine
    // what workflow to invoke.  Or you may have one giant workflow which takes
    // care of everything!.
    
        return new ${PROC_CLASS_NAME}(job);    
    }

    public ${CLASS_NAME}(FlowBaseEngine eng) {
        super(eng);
    }

    @Override
    protected void onShutdown() {
        // add specific code to handle shutdown
    }
    
}

