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
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


public class AuthWork {
	
	public static Activity getAuthMtd(String type) {
		
		if ("facebook".equals(type)) return new PTask(facebook_login);
		if ("google+".equals(type)) return new PTask(gplus_login);
		if ("openid".equals(type)) return new PTask(openid_login);
		
		return new PTask(db_login);		
	}
	
	private static Work facebook_login = new Work() {
		public void eval(Job job, Object arg) {
			System.out.println("using facebook to login.\n");
		}
	};
	
	private static Work gplus_login = new Work() {
		public void eval(Job job, Object arg) {
			System.out.println("using google+ to login.\n");
		}
	};
	
	private static Work openid_login = new Work() {
		public void eval(Job job, Object arg) {
			System.out.println("using open-id to login.\n");
		}
	};
	
	private static Work db_login = new Work() {
		public void eval(Job job, Object arg) {
			System.out.println("using internal db to login.\n");
		}
	};
	
}