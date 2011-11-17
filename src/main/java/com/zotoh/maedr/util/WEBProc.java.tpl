package ${PACKAGE_ID};


import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.*;
import com.zotoh.maedr.wflow.*;


public class WEBProcessor extends MiniWFlow {

    private Work task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
                    ServletEventResult res= new ServletEventResult();
                    Event ev= job.getEvent();                    
                    res.setData("<html><body>Bonjour!</body></html>");
                    ev.setResult(res) ;                            
        }
    };

    protected Activity onStart() {
        
        return new PTask( task1);
    }

    
    
    public WEBProcessor(Job j) {
        super(j);
    }
    
    
}
