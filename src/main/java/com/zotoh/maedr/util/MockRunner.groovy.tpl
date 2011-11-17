package ${PACKAGE_ID};


import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.zotoh.core.io.StreamUte;
import com.zotoh.maedr.wflow.FlowBaseEngine;


class MockRunner {

    static void main( args) {
    
        System.setProperty("user.dir", "${APP.DIR}");
        System.getProperties().put("log4j.configuration", "${LOG4J.REF}");
             
        try {           
            def props= new Properties() ,
            eng= new FlowBaseEngine() ,
            inp= null;
             
            try {
                inp= StreamUte.readStream(new File("${MANIFEST.FILE}"));
                props.load(inp);
            }
            finally {
                StreamUte.close(inp);
            }
            
            eng.start(props);
        }
        catch (Throwable t) {
            t.printStackTrace();            
        }
    }

}

