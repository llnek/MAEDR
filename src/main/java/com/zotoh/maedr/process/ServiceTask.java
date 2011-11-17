package com.zotoh.maedr.process;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.WorkUnit;

/**
 * @author kenl
 *
 */
public class ServiceTask extends WorkUnit {

    /**
     * @param proc
     */
    public ServiceTask(Processor proc) {
        super(proc, "t1");
    }

    @Override
    protected FlowInfo evalOneStep(Job job, Object closureObject)
            throws Exception {
        return null;
    }

}
