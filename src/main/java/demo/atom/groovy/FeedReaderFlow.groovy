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
  

package demo.atom;


import java.util.Iterator;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.FeedEvent;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


/**
 * @author kenl
 *
 */
class FeedReaderFlow extends MiniWFlow {

    def task1=new Work() {
        void eval(Job job, Object closure) {
            
            def ev= job.getEvent();            
            def it;
            def feed= ev.getFeedData();

            println("===> Title: " + feed.getTitle());
            println("===> Author: " + feed.getAuthor());
            println("===> Description: " + feed.getDescription());
            println("===> Pub date: " + feed.getPublishedDate());
            println("===> Copyright: " + feed.getCopyright());
            println("===> Modules used:");
            for ( it = feed.getModules().iterator(); it.hasNext();) {
                println("\t" + it.next().getUri());
            }
            println("===> Titles of the " + feed.getEntries().size() + " entries:");
            for ( it = feed.getEntries().iterator(); it.hasNext();) {
                println("\t" + it.next().getTitle());
            }
            if (feed.getImage() != null) {
                println("===> Feed image URL: " + feed.getImage().getUrl());
            }

            
            println("\nPRESS Ctrl-C anytime to end program.\n");
            
            
            
        }
    };
    
            
	/**/
    def FeedReaderFlow(Job j) {
        super(j);
    }

    def Activity onStart() {
        return new PTask(task1);
    }
    
	public static class Preamble extends MiniWFlow {
		def Preamble(Job j) { super(j); }
		def Activity onStart() {
			return new PTask( new Work() {
				void eval(Job job, Object closure) {
					println("Preparing to pull down RSS feeds...");
				}
			});
		}
	}

}
