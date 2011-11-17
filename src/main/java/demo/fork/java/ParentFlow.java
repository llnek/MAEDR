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
  
package demo.fork.java;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.And;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Split;
import com.zotoh.maedr.wflow.Work;

/**
 * @author kenl
 * 
    parent(s1) --> split&nowait 
                   |-------------> child(s1)----> split&wait --> grand-child 
                   |                              |                    |
                   |                              |<-------------------+
                   |                              |---> child(s2) -------> end
                   |
                   |-------> parent(s2)----> end
 */
public class ParentFlow extends MiniWFlow {
    
    Work parent1= new Work() {
        public void eval(Job job, Object closure) {
            System.out.println("Hi, I am the parent");
        }
    };
    Work parent2= new Work() {
        private int fib(int n) {
            if (n <3) return 1;
            return fib(n-2) + fib(n-1);
        }
        public void eval(Job job, Object closure) {
            System.out.println("Parent: after fork, continue to calculate fib(6)...");
            System.out.print("Parent: ");
            for (int i=1; i <= 6; ++i) {
                System.out.print( fib(i) + " ");
            }
            System.out.println();
        }
    };
    Work gchild= new Work() {
        public void eval(Job job, Object closure) throws Exception {
            System.out.println("Grand-child: taking some time to do this task... ( ~ 6secs)");
            for (int i=0; i < 6; ++i) {
                    Thread.sleep(1000);
                    System.out.print("...");
            }
            System.out.println("");
            int rhs= (Integer)job.getData("rhs") ;
            int lhs= (Integer)job.getData("lhs") ;
            job.setData("result", lhs*rhs);
        }
    };
    Work child=new Work() {
        public void eval(Job job, Object closure) {
            System.out.println("Child: I am a child, created by my parent");
            System.out.println("Child: I will create my own child (blocking)");
            job.setData("rhs", 60);
            job.setData("lhs", 5);
            final PTask p2= new PTask(new Work() {
                public void eval(Job job, Object closure) {
                    System.out.println("Child: the result for (5 * 60) according to my own child is = " 
                            +  job.getData("result"));
                    System.out.println("\nPRESS Ctrl-C anytime to end program.\n");                        
                }
            });
            Activity a= new Split(
                    // split & wait
                    new And().withBody(p2)
            )
            .addSplit(new PTask(gchild));
            
            setResult(a);
        }
    };
    
    
	/**
	 * @param j
	 */
	public ParentFlow(Job j) {
		super(j);
	}

    @Override
    protected Activity onStart() {
        
        PTask parent= new PTask(parent1);        
        PTask fib= new PTask(parent2);        
        PTask ch= new PTask(child);
        
        return parent.chain(
                // split but no wait
                new Split().addSplit(ch))
                // parent continues;
                .chain(fib);
    }






    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Demo fork(split)/join of tasks..." );
                }
            });
        }
    }


}
