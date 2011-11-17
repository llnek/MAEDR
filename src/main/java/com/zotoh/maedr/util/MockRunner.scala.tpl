package ${PACKAGE_ID}


import java.io.{InputStream, File}
import java.util.Properties

import com.zotoh.core.io.StreamUte
import com.zotoh.maedr.wflow.FlowBaseEngine


object MockRunner {

  def main(args:Array[String]) = {
  
        System.setProperty("user.dir", "${APP.DIR}")
        System.getProperties().put("log4j.configuration", "${LOG4J.REF}")
           
      try {
      
        val eng= new FlowBaseEngine()
        val props= new Properties()
        val inp= StreamUte.readStream(new File("${MANIFEST.FILE}"))
        try {
          props.load(inp)
        } finally {
          StreamUte.close(inp)
        }
        eng.start(props)
      }
      catch {
        case e : Throwable => e.printStackTrace()
      }
  }



}


