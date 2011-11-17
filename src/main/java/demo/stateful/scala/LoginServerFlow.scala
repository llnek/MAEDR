/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
  
package demo.stateful

import _root_.java.util.{Date,List,ArrayList}
import _root_.java.text.SimpleDateFormat

import _root_.scala.xml.Node

import org.json.{JSONArray,JSONObject}
import com.zotoh.maedr.device.{HttpEvent,HttpEventResult}
import com.zotoh.core.io.StreamData
import com.zotoh.core.util.{StrArr,StrUte}
import com.zotoh.maedr.core.{Job,ColPicker,WState}
import com.zotoh.netio.HTTPStatus
import com.zotoh.maedr.wflow._



/**
 * @author kenl
 *
 */
class LoginServerFlow(job:Job) extends MiniWFlow(job) {

    val task1= new Work() {
        override def eval(job:Job, closure:Object) {
            val ev= job.getEvent().asInstanceOf[HttpEvent]
            val a=ev.getParam("user") 
            val user= if(a== null) null else a.getFirst()
            if (!StrUte.isEmpty(user)) { 
              val f=getCurStep().getFlow()
              f.retrievePreviousState(ColPicker.KEYID, user)
              val ls= f.getState()
              ls.setKey(user)
              var arr= ls.getRoot().optJSONArray("dates")
              if (arr == null) {
                  arr=new JSONArray()
                  ls.getRoot().put("dates", arr)
              }
              arr.put(new Date().getTime())
          }
        }
    }
    
    val task2= new Work() {
        override def eval(job:Job , closure:Object ) {
            val ls= getCurStep().getFlow().getState()
            val r= new HttpEventResult()
            val list= collection.mutable.ListBuffer.empty[Node]
            val arr= ls.getRoot().optJSONArray("dates")
            if (arr != null) {
                val len= arr.length()-1
                for (i <- 0 to len) {
                    val str= new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss.SSSZ").format(new Date( arr.optLong(i)))
                    list += <li><i>{str}</i></li>
                }
            }

            val resp= new StreamData()
            val bf=         <html>
            <h1>Shows user: {ls.getKey()}'s login history...</h1>
            <p><ul>
            { list }
            <br/>
            </ul></p>
            </html>.buildString(false)

            resp.resetMsgContent( bf.getBytes("utf-8"))
            // pass response into the result object        
            r.setStatus(HTTPStatus.OK)
            r.setData(resp)
            job.getEvent().setResult(r)
            
            
            println("\nPRESS Ctrl-C anytime to end program.\n")
            println("After restart, When you point your browser again to the server with")
            println("the same user, you should get the complete history back")
            
        }        
    }
    
    override def preStart() : Unit = {    		
		println(
	        "\n\n-> Point your browser to http://<hostname>:8080/?user=some-name")
    }

    override def onEnd() : Unit = {
        val s= getState().getKey().asInstanceOf[String]
        if ( ! StrUte.isEmpty(s)) { 
            persistState()
        } 
    }
    
    override def onStart() : Activity = {
        new PTask(task1).chain( new PTask(task2))
    }
    
}

class LoginServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                println("Point your browser to http://" +
                    com.zotoh.netio.NetUte.getLocalHost() +
                    ":8080/test/helloworld?user=joe"
                );
            }
        });
    }
}

