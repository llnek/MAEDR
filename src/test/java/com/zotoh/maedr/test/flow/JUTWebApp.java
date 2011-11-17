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

package com.zotoh.maedr.test.flow;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.And;
import com.zotoh.maedr.wflow.Block;
import com.zotoh.maedr.wflow.BoolExpr;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Split;
import com.zotoh.maedr.wflow.Switch;
import com.zotoh.maedr.wflow.SwitchChoiceExpr;
import com.zotoh.maedr.wflow.While;
import com.zotoh.maedr.wflow.Work;

/**/
public class JUTWebApp extends BaseJUT {
    
    /**/
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUTWebApp.class);
    }

    public static class TestApp extends MiniWFlow { 
    		public TestApp(Job j) {super(j); }
    		StringBuilder BUF=new StringBuilder();
    		
    		protected void onEnd() {
    			getEngine().getProperties().put("result", BUF.toString());
    			getEngine().shutdown();
    		}
    		
	    Work facebook_login=new Work() {
	    		public void eval(Job job,Object closure) {
	    			BUF.append("facebook\n");
	    		}
	    };
	    Work gplus_login=new Work() {
    			public void eval(Job job,Object closure) {
	    			BUF.append("google+\n");    				
    			}
	    };
	    Work openid_login=new Work() {
			public void eval(Job job,Object closure) {
    				BUF.append("openid\n");    								
			}
	    };
	    Work db_login=new Work() {
			public void eval(Job job,Object closure) {				
				BUF.append("dbase\n");    								
			}
	    };
	    
	    Activity AuthUser=new Switch()
	    .withChoice("facebook", new PTask(facebook_login))
	    .withChoice("google+", new PTask(gplus_login))
	    .withChoice("openid", new PTask(openid_login))
	    .withDef(new PTask(db_login))
	    .withExpr(new SwitchChoiceExpr() {
	    		public Object eval(Job j) {
	    			return "facebook"; // hard code to use facebook
	    		}
	    });
	    
	    Work get_profile=new Work() {
	    		public void eval(Job j, Object closure) {
	    			BUF.append("superuser\n");
	    		}
	    };
	    Activity GetProfile=new PTask(get_profile);
	    
	    Work perm_ami=new Work() {
	    		public void eval(Job j, Object closure) {
    				Integer n= (Integer) j.getData("ami_count") ;
    				if (n != null && n==2) {
    					BUF.append("permami\n");
    				}
	    		}
	    };
	    Activity prov_ami=new While().withBody(new PTask(perm_ami))
	    				.withExpr(new BoolExpr(){
	    					public boolean eval(Job j) {
	    						Integer n= (Integer) j.getData().getData("ami_count");
	    						if (n==null) { n=0; }
	    						else { ++n; }
	    						j.getData().setData("ami_count", n);
	    						return n < 3;
	    					}
	    				});
	    
	    Work perm_vol=new Work() {
    			public void eval(Job j, Object closure) {
    				Integer n= (Integer) j.getData("vol_count") ;
    				if (n != null && n==2) {
    					BUF.append("permvol\n");
    				}
    			}
	    };
	    Activity prov_vol=new While().withBody(new PTask(perm_vol))
			.withExpr(new BoolExpr(){
				public boolean eval(Job j) {
					Integer n= (Integer) j.getData().getData("vol_count");
					if (n==null) { n=0; }
					else { ++n; }
					j.getData().setData("vol_count", n);
					return n < 3;
				}
			});
	    
	    Work write_db=new Work() {
    			public void eval(Job j, Object closure) {
    				Integer n= (Integer) j.getData("wdb_count") ;
    				if (n != null && n==2) {
        				BUF.append("writedb\n");
    				}
    			}
	    };
	    Activity save_sdb=new While().withBody(new PTask(write_db))
		.withExpr(new BoolExpr(){
			public boolean eval(Job j) {
				Integer n= (Integer) j.getData().getData("wdb_count");
				if (n==null) { n=0; }
				else { ++n; }
				j.getData().setData("wdb_count", n);
				return n < 3;
			}
		});
	    
	    Activity Provision= new Split().addSplit(prov_ami)
	    				.addSplit(prov_vol)
	    				.withJoin(new And().withBody(save_sdb));
	    
	    Work reply_user=new Work(){
	    		public void eval(Job j, Object closure) {
	    			BUF.append("200-OK\n");
	    		}
	    };
	    
	    Activity ReplyUser=new PTask(reply_user);
	    
	    
	    
	    protected Activity onStart() {
	        return new Block().chain(AuthUser)
	        				.chain(GetProfile)
	        				.chain(Provision)
	        				.chain(ReplyUser);
	    }
	    
	    
    }
    
    @Test
    public void test1() throws Exception {
    		Properties props=create_props("com.zotoh.maedr.test.flow.JUTWebApp$TestApp"); 
    		props.put("_", this);
    		_eng.start(props );
    		
    		String res= _eng.getProperties().getProperty("result");
    		boolean b=false;
    		if (res != null) {
    			b=res.indexOf("facebook") >= 0 &&
    			res.indexOf("superuser") >= 0 &&
    			res.indexOf("permami") >= 0 &&
    			res.indexOf("permvol") >= 0 &&
    			res.indexOf("writedb") >= 0 &&
    			res.indexOf("200-OK") >= 0 ;
    		}

    		assertTrue(b);
    }
    
    @Test
    public void testDummy() throws Exception {}
  
}
