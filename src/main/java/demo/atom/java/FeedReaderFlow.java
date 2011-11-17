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
  

package demo.atom.java;

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
public class FeedReaderFlow extends MiniWFlow {

    Work task1=new Work() {
        public void eval(Job job, Object closure) {
            
            
            FeedEvent ev= (FeedEvent) job.getEvent();            
            SyndFeed feed= ev.getFeedData();

            System.out.println("===> Title: " + feed.getTitle());
            System.out.println("===> Author: " + feed.getAuthor());
            System.out.println("===> Description: " + feed.getDescription());
            System.out.println("===> Pub date: " + feed.getPublishedDate());
            System.out.println("===> Copyright: " + feed.getCopyright());
            System.out.println("===> Modules used:");
            for (Iterator<?> it = feed.getModules().iterator(); it.hasNext();) {
                System.out.println("\t" + ((Module)it.next()).getUri());
            }
            System.out.println("===> Titles of the " + feed.getEntries().size() + " entries:");
            for (Iterator<?> it = feed.getEntries().iterator(); it.hasNext();) {
                System.out.println("\t" + ((SyndEntry)it.next()).getTitle());
            }
            if (feed.getImage() != null) {
                System.out.println("===> Feed image URL: " + feed.getImage().getUrl());
            }

            
            System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
            
            
            
        }
    };
    
            
	/**/
    public FeedReaderFlow(Job j) {
        super(j);
    }

    @Override
    protected Activity onStart() {
        return new PTask(task1);
    }
    
    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Preparing to pull down RSS feeds...");                    
                }
            });
        }
    }
    
}
