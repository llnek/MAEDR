package ${PACKAGE_ID};

import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.core.JobData;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.*;

class ${CLASS_NAME} extends MiniWFlow {

    private def task1=new Work() {
        public void eval(Job job, Object closure) {
            def ev= job.getEvent();
                    // do your stuff here            
            println("hello world");                                                               
        }
    };

	protected Activity onStart() {
	
	   return new PTask( task1 );
    }




    def ${CLASS_NAME}(Job job) {
        super(job);
    }


}

