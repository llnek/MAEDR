package ${PACKAGE_ID};

import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.core.JobData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.*;


public class ${CLASS_NAME} extends MiniWFlow {

    private Work task1=new Work() {
        public void eval(Job job, Object closure) {
            Event ev= job.getEvent();
                    // do your stuff here            
                                System.out.println("hello world");                                           
                    
        }
    };

    @Override
    protected Activity onStart() {
        
        return new PTask( task1);
        
    }




    public ${CLASS_NAME}(Job job) {
        super(job);
    }







}

