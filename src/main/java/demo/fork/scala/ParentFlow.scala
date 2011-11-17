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
  
package demo.fork

import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._

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
class ParentFlow(job:Job) extends MiniWFlow(job) {
    
    val parent1= new Work() {
        override def eval(job:Job, closure:Object) {
            println("Hi, I am the parent")
        }
    }
    val parent2= new Work() {
        override def eval(job:Job, closure:Object) {
          def fib(n:Int) : Int = { 
              if (n <3) { 
                1 } else { 
                  fib(n-2) + fib(n-1);
              }
            }
            println("Parent: after fork, continue to calculate fib(6)...")
            print("Parent: ")
            for (i <- 1 to 6) {
                print( fib(i) + " ")
            }
            println()
        }
    }
    val gchild= new Work() {
        override def eval(job:Job, closure:Object) {
            println("Grand-child: taking some time to do this task... ( ~ 6secs)")
            for (i <- 1 to 6) {
                    Thread.sleep(1000)
                    print("...")
            }
            println("")
            val rhs = job.getData("rhs").asInstanceOf[Int]
            val lhs = job.getData("lhs").asInstanceOf[Int]
            job.setData("result", lhs*rhs)
        }
    }
    val child=new Work() {
        override def eval(job:Job, closure:Object) {
            println("Child: I am a child, will create my own child (blocking)")
            job.setData("rhs", 60)
            job.setData("lhs", 5)
            val p2= new PTask(new Work() {
                override def eval(job:Job, closure:Object) {
                    println("Child: the result for (5 * 60) according to my own child is = "  +
                            job.getData("result"))
                    println("\nPRESS Ctrl-C anytime to end program.\n")
                }
            })
            val a= new Split(
                    // split & wait
                    new And().withBody(p2)
            )
            .addSplit(new PTask(gchild))
            
            setResult(a)
        }
    }
    
    override def onStart() : Activity = {
        
        val parent= new PTask(parent1)
        val fib= new PTask(parent2)
        val ch= new PTask(child)
        
        parent.chain(
                // split but no wait
                new Split().addSplit(ch))
                // parent continues;
                .chain(fib)
    }








}

class ParentFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo fork(split)/join of tasks..." )
            }
        })
    }
}



