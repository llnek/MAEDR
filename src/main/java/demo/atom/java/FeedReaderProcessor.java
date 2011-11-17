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
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.FeedEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;


/**
 * @author kenl
 *
 */
public class FeedReaderProcessor extends Stateless {

	/**/
    public FeedReaderProcessor(Job j) {
        super(j);
    }

    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
            case 10001: return new Task1(this);
        }
        return WorkUnit.NONE;
    }

    protected FlowInfo onStart(Event e) {
	    	//	do some initial stuff ?
	    	// 	then tell runtime what task to start with
	    return new FlowInfo(10001);
    }
    
        
    /**/
    private class Task1 extends WorkUnit {

        public Task1(Processor proc) {
            super(proc, "task1");
        }

        protected FlowInfo evalOneStep(Job job, Object closureArg) throws Exception {
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
            
            // done - end.
            return FlowInfo.END;
        }
        
    }
    
    
}
