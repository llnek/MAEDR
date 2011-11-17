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
  
package demo.timer.java;

import java.util.Date;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.TimerEvent;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


/**
 * @author kenl
 *
 */
public class TimeServerFlow extends MiniWFlow {
    
    private static int _count=0;
    
    Work task1= new Work() {
        public void eval(Job job, Object closure) {
            TimerEvent ev= (TimerEvent) job.getEvent();                
            if ( ev.isRepeating()) {
                if (_count < 5) {
                    System.out.println("-----> repeating-update: " + new Date()) ;
                    ++_count;
                }
                if (_count >= 5) {
                        System.out.println("\nPRESS Ctrl-C anytime to end program.\n");
                }
            }
            else {
                System.out.println("-----> once-only!!: " + new Date()) ;            
            }
        }
    };
    
    public TimeServerFlow(Job j) {
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
                    System.out.println("Demo various timer functions..." );
                }
            });
        }
    }

}
