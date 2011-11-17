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

package demo.multistep

import com.zotoh.maedr.core.Job
import com.zotoh.maedr.wflow._






/**
 * What this example demostrates is a webservice which takes in some user info, authenticate the
 * user, then perform some EC2 operations such as granting permission to access an AMI, and
 * permission to access/snapshot a given volume.  When all is done, a reply will be sent back
 * to the user.
 * 
 * This flow showcases the use of conditional activities such a Switch() & If().  Shows how to loop using
 * While(), and how to use Split & Join. 
 * 
 * @author kenl
 * 
 */
class MultiStepFlow(job:Job) extends MiniWFlow(job) {

	override def onEnd() : Unit = {
		// we override this just to show/indicate that we are done.
		println("Finally, we are done.!")
	}
	
	// step1. choose a method to authenticate the user
	// here, we'll use a switch() to pick which method
	private val AuthUser = new Switch()
					.withChoice("facebook", AuthWork.getAuthMtd("facebook"))
					.withChoice("google+", AuthWork.getAuthMtd("google+"))
					.withChoice("openid", AuthWork.getAuthMtd("openid"))
					.withDef(AuthWork.getAuthMtd("db"))
					.withExpr(new SwitchChoiceExpr() {
						def eval(j:Job) : Object = {
							// hard code to use facebook in this example, but you
							// could check some data from the job, such as URI/Query params
							// and decide on which mth-value to switch() on.
							"facebook"
						}
					})
	
	// step2.
	private val get_profile = new Work() {
		def eval(j:Job, arg:Object) {
			println("the user is deemed as superuser.\n")
		}
	}
	private val GetProfile = new PTask(get_profile)

	// step3. we are going to dummy up a retry of 2 times to simulate network/operation
	// issues encountered with EC2 while trying to grant permission.
	// so here , we are using a while() to do that.
	private val perm_ami = new Work() {
		def eval(j:Job, arg:Object) {
			val n = j.getData("ami_count")
			if (n != null && n.asInstanceOf[Integer] == 2) {
				println("granted permission for user to launch this ami(id).\n")
			}
		}
	}
	private val prov_ami = new While().withBody(new PTask(perm_ami)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						def eval(j:Job) : Boolean = {
							val n = j.getData("ami_count")
                            var c=0
							if (n != null) {
								c = n.asInstanceOf[Integer] + 1
							}
							j.setData("ami_count", c)
							// we are going to dummy up so it will retry 2 times
							println("Failed to contact ami- server, will retry again... ("+c+") ");							
							c < 3
						}
					})

	// step3'. we are going to dummy up a retry of 2 times to simulate network/operation
	// issues encountered with EC2 while trying to grant volume permission.
	// so here , we are using a while() to do that.	
	private val perm_vol = new Work() {
		def eval(j:Job, arg:Object) : Unit= {
			val n = j.getData("vol_count")
			if (n != null && n.asInstanceOf[Integer] == 2) {
				println("granted permission for user to access/snapshot this volume(id).\n")
			}
		}
	}
	private val prov_vol = new While().withBody(new PTask(perm_vol)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						def eval(j:Job) : Boolean = {
							val n = j.getData( "vol_count")
                            var c=0
							if (n != null) {
								c = n.asInstanceOf[Integer] + 1
							}
							j.setData("vol_count", c)
							// we are going to dummy up so it will retry 2 times
							println("Failed to contact vol- server, will retry again... ("+c+") ");							
							c < 3
						}
					})

	// step4. pretend to write stuff to db. again, we are going to dummy up the case
	// where the db write fails a couple of times.
	// so again , we are using a while() to do that.	
	private val write_db = new Work() {
		def eval(j:Job, arg:Object) :Unit = {
			val n = j.getData("wdb_count")
			if (n != null && n.asInstanceOf[Integer] == 2) {
				println("wrote stuff to database successfully.\n")
			}
		}
	}
	private val save_sdb = new While().withBody(new PTask(write_db)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						def eval(j:Job) : Boolean = {
							val n = j.getData("wdb_count")
                            var c=0
							if (n != null) {
								c = n.asInstanceOf[Integer] + 1
							}
							j.setData("wdb_count", c)
							// we are going to dummy up so it will retry 2 times
							println("Failed to contact db- server, will retry again... ("+c+") ");							
							c < 3
						}
					})

	// this is the step where it will do the provisioning of the AMI and the EBS volume
	// in parallel.  To do that, we use a split-we want to fork off both tasks in parallel.  Since
	// we don't want to continue until both provisioning tasks are done. we use a AndJoin to hold/freeze
	// the workflow.
	private val Provision = new Split().addSplit(prov_ami).addSplit(prov_vol)
					.withJoin(new And().withBody(save_sdb))

	// this is the final step, after all the work are done, reply back to the caller.
	// like, returning a 200-OK.
	private val reply_user = new Work() {
		def eval(j:Job, arg:Object) : Unit= {
			println("we'd probably return a 200 OK back to caller here.\n")
		}
	}
	private val ReplyUser = new PTask(reply_user)

	private val error_user = new Work() {
		def eval(j:Job, arg:Object) : Unit= {
			println("we'd probably return a 200 OK but with errors.\n")
		}
	}
	private val ErrorUser = new PTask(error_user)
	

	// do a final test to see what sort of response should we send back to the user.
	private val FinalTest = new If()
		.withThen(ReplyUser)
		.withElse(ErrorUser)
		.withExpr(new BoolExpr() {
			def eval(j:Job ) : Boolean = {
				// we hard code that all things are well.
				true
			}			
		})
		
	

	// returning the 1st step of the workflow.	
	def onStart() : Activity = {
		
		// so, the workflow is a small (4 step) workflow, with the 3rd step (Provision) being
		// a split, which forks off more steps in parallel.
		
		new Block().chain(AuthUser).chain(GetProfile).chain(Provision)
						.chain(FinalTest)
	}

}

class MultiStepFlowPreamble(j:Job) extends MiniWFlow(j) {
    override def onStart() : Activity = {
        new PTask( new Work() {
            override def eval(j:Job, arg:Object ) : Unit = {
                    println("Demo a set of workflow control features..." )
            }
        })
    }
}


