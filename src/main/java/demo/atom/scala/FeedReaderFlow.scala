/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
  

package demo.atom

import _root_.scala.collection.JavaConverters._

import com.sun.syndication.feed.synd.{SyndFeed,SyndEntry}
import com.sun.syndication.feed.module.Module
import com.zotoh.maedr.device.FeedEvent
import com.zotoh.maedr.wflow._
import com.zotoh.maedr.core.Job



/**
 * @author kenl
 *
 */
class FeedReaderFlow(job:Job) extends MiniWFlow(job) {

    val task1=new Work() {
        override def eval(job:Job, closure:Object) {
            
            val ev = job.getEvent().asInstanceOf[FeedEvent];
            val feed= ev.getFeedData()

            println("===> Title: " + feed.getTitle())
            println("===> Author: " + feed.getAuthor())
            println("===> Description: " + feed.getDescription())
            println("===> Pub date: " + feed.getPublishedDate())
            println("===> Copyright: " + feed.getCopyright())
            println("===> Modules used:")

            //val it : Iterator[_] = feed.getModules().iterator().asScala
            feed.getModules().iterator().asScala.map { a : Any =>
              println( "\t" + a.asInstanceOf[Module].getUri() ) 
            }

            println("===> Titles of the " + feed.getEntries().size() + " entries:")

            feed.getEntries().iterator().asScala.map { a : Any =>
              println("\t" + a.asInstanceOf[SyndEntry].getTitle())
            }

            if (feed.getImage() != null) {
                println("===> Feed image URL: " + feed.getImage().getUrl())
            }
            
            println("\nPRESS Ctrl-C anytime to end program.\n")
            
        }
    }
    
            
    override def onStart() : Activity = {
        new PTask(task1)
    }
    
    
}


class FeedReaderFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
					println("Preparing to pull down RSS feeds...")
            }
        })
    }
}

