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
  
package demo.async

import com.zotoh.core.util.LoggerFactory.getLogger
import com.zotoh.core.util.Logger
import com.zotoh.maedr.core.AsyncCallback


/**
 * @author kenl
 *
 */
class DummyAsyncWS {
    
    val _log  = getLogger(classOf[DummyAsyncWS])
    def tlog()  = {   _log    }    
    
    def doLongAsyncCall( cb:AsyncCallback ) : Unit = {
        val t= new Thread(new Runnable() {
            override def run() {
                Thread.sleep(10000)
                cb.onSuccess("hello world")
            }
        })
        t.setDaemon(true)
        t.start()
    }
    
}
