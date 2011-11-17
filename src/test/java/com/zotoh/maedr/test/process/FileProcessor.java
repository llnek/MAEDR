/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.test.process;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.FileEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class FileProcessor extends Stateless {

    public FileProcessor(Job j)     {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit()     {
        int pos = getCurStepPos();
        WorkUnit bs= null;
        
        switch (pos) {
            case 909090:
                bs= new FileTask(this);
            break;
        }
        
        return bs;
    }

    @Override
    protected void createState() {
    }

    @Override
    protected FlowInfo onStart(Event ev)     {
        return new FlowInfo("c1", 909090 );
    }

    @Override
    protected void onEnd()     {
    }

    @Override
    protected FlowInfo onError(Exception e)     {
        return null;
    }

}


class FileTask extends WorkUnit {
    public FileTask(Processor proc )     {
        super(proc, "file-task");
    }
    @Override
    protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception     {
        FileEvent ev= (FileEvent) job.getEvent();
        tlog().debug("File-Task: file name = {}" , ev.getFile().getCanonicalPath());
        return getProcessor().moveToEnd();
    }    
}
