package ${PACKAGE_ID};


import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.ServletEvent;
import com.zotoh.maedr.device.ServletEventResult;
import com.zotoh.maedr.wflow.*;


class WEBProcessor extends MiniWFlow {

    private def task1= new Work() {
        public void eval(Job job, Object closure) throws Exception {
                    def res= new ServletEventResult();
                    def ev= job.getEvent();                    
                    res.setData("<html><body>Bonjour!</body></html>");
                    ev.setResult(res) ;                            
        }
    };
    
    protected Activity onStart() {
        
        return new PTask( task1);
    }

    
    
    def WEBProcessor(Job j) {
        super(j);
    }
    
    
}
