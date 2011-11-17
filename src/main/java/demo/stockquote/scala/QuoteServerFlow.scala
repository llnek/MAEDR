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
  

package demo.stockquote

import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._

/**
 * @author kenl
 *
 */
class QuoteServerFlow(job:Job) extends MiniWFlow(job) {

    val show_quote=new Work() {
        override def eval(job:Job, arg:Object ) : Unit = {
            val ev= job.getEvent().asInstanceOf[StockQuoteEvent]
            println( "*******************************************")
            println( "Symbol: " + ev.ticker)
            println( "Price: " + ev.price)
            println( "Change: " + ev.change)
            println( "===========================================")
        }
    }
    
    
    override def onStart() : Activity =  {
        new PTask(show_quote)
    }

}

class QuoteServerFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo reading stock quotes..." )
            }
        })
    }
}


