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

package demo.multistep.java;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.And;
import com.zotoh.maedr.wflow.Block;
import com.zotoh.maedr.wflow.BoolExpr;
import com.zotoh.maedr.wflow.If;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Split;
import com.zotoh.maedr.wflow.Switch;
import com.zotoh.maedr.wflow.SwitchChoiceExpr;
import com.zotoh.maedr.wflow.While;
import com.zotoh.maedr.wflow.Work;

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
public class MultiStepFlow extends MiniWFlow {

	public MultiStepFlow(Job j) {		super(j);	}

	protected void onEnd() {
		// we override this just to show/indicate that we are done.
		System.out.println("Finally, we are done.!");
	}
	
	// step1. choose a method to authenticate the user
	// here, we'll use a switch() to pick which method
	Activity AuthUser = new Switch()
					.withChoice("facebook", AuthWork.getAuthMtd("facebook"))
					.withChoice("google+", AuthWork.getAuthMtd("google+"))
					.withChoice("openid", AuthWork.getAuthMtd("openid"))
					.withDef(AuthWork.getAuthMtd("db"))
					.withExpr(new SwitchChoiceExpr() {
						public Object eval(Job j) {
							// hard code to use facebook in this example, but you
							// could check some data from the job, such as URI/Query params
							// and decide on which mth-value to switch() on.
							return "facebook"; 
						}
					});
	
	// step2.
	Work get_profile = new Work() {
		public void eval(Job j, Object closure) {
			System.out.println("the user is deemed as superuser.\n");
		}
	};
	Activity GetProfile = new PTask(get_profile);

	// step3. we are going to dummy up a retry of 2 times to simulate network/operation
	// issues encountered with EC2 while trying to grant permission.
	// so here , we are using a while() to do that.
	Work perm_ami = new Work() {
		public void eval(Job j, Object closure) {
			Integer n = (Integer) j.getData("ami_count");
			if (n != null && n == 2) {
				System.out.println("granted permission for user to launch this ami(id).\n");
			}
		}
	};
	Activity prov_ami = new While().withBody(new PTask(perm_ami)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						public boolean eval(Job j) {
							Integer n = (Integer) j.getData().getData("ami_count");
							if (n == null) {
								n = 0;
							} else {
								++n;
							}
							j.getData().setData("ami_count", n);
							// we are going to dummy up so it will retry 2 times
							System.out.println("Failed to contact ami- server, will retry again... ("+n+") ");
							return n < 3;
						}
					});

	// step3'. we are going to dummy up a retry of 2 times to simulate network/operation
	// issues encountered with EC2 while trying to grant volume permission.
	// so here , we are using a while() to do that.	
	Work perm_vol = new Work() {
		public void eval(Job j, Object closure) {
			Integer n = (Integer) j.getData("vol_count");
			if (n != null && n == 2) {
				System.out.println("granted permission for user to access/snapshot this volume(id).\n");
			}
		}
	};
	Activity prov_vol = new While().withBody(new PTask(perm_vol)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						public boolean eval(Job j) {
							Integer n = (Integer) j.getData().getData( "vol_count");
							if (n == null) {
								n = 0;
							} else {
								++n;
							}
							j.getData().setData("vol_count", n);
							// we are going to dummy up so it will retry 2 times
							System.out.println("Failed to contact vol- server, will retry again... ("+n+") ");
							return n < 3;
						}
					});

	// step4. pretend to write stuff to db. again, we are going to dummy up the case
	// where the db write fails a couple of times.
	// so again , we are using a while() to do that.	
	Work write_db = new Work() {
		public void eval(Job j, Object closure) {
			Integer n = (Integer) j.getData("wdb_count");
			if (n != null && n == 2) {
				System.out.println("wrote stuff to database successfully.\n");
			}
		}
	};
	Activity save_sdb = new While().withBody(new PTask(write_db)).withExpr(
					// the while (test-condition)
					new BoolExpr() {
						public boolean eval(Job j) {
							Integer n = (Integer) j.getData().getData("wdb_count");
							if (n == null) {
								n = 0;
							} else {
								++n;
							}
							j.getData().setData("wdb_count", n);
							// we are going to dummy up so it will retry 2 times
							System.out.println("Failed to contact db- server, will retry again... ("+n+") ");
							return n < 3;
						}
					});

	// this is the step where it will do the provisioning of the AMI and the EBS volume
	// in parallel.  To do that, we use a split-we want to fork off both tasks in parallel.  Since
	// we don't want to continue until both provisioning tasks are done. we use a AndJoin to hold/freeze
	// the workflow.
	Activity Provision = new Split().addSplit(prov_ami).addSplit(prov_vol)
					.withJoin(new And().withBody(save_sdb));

	// this is the final step, after all the work are done, reply back to the caller.
	// like, returning a 200-OK.
	Work reply_user = new Work() {
		public void eval(Job j, Object closure) {
			System.out.println("we'd probably return a 200 OK back to caller here.\n");
		}
	};
	Activity ReplyUser = new PTask(reply_user);

	Work error_user = new Work() {
		public void eval(Job j, Object closure) {
			System.out.println("we'd probably return a 200 OK but with errors.\n");
		}
	};
	Activity ErrorUser = new PTask(error_user);
	

	// do a final test to see what sort of response should we send back to the user.
	Activity FinalTest = new If()
		.withThen(ReplyUser)
		.withElse(ErrorUser)
		.withExpr(new BoolExpr() {
			public boolean eval(Job job) {
				// we hard code that all things are well.
				return true;
			}			
		});
		
	

	// returning the 1st step of the workflow.	
	protected Activity onStart() {
		
		// so, the workflow is a small (4 step) workflow, with the 3rd step (Provision) being
		// a split, which forks off more steps in parallel.
		
		return new Block().chain(AuthUser).chain(GetProfile).chain(Provision)
						.chain(FinalTest);
	}




    public static class Preamble extends MiniWFlow {
        public Preamble(Job j) { super(j); }
        protected Activity onStart() {
            return new PTask( new Work() {
                public void eval(Job job, Object closure) throws Exception {
                    System.out.println("Demo a set of workflow control features..." );
                }
            });
        }
    }



}
