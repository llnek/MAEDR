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

package demo.multistep;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;


class AuthWork {
	
	static def getAuthMtd(type) {
		
        switch (type) {
            case "facebook": return new PTask(facebook_login);
            case "google+": return new PTask(gplus_login);
            case "openid": return new PTask(openid_login);
        }
		
		return new PTask(db_login);		
	}
	
	private static def facebook_login = new Work() {
		void eval(Job job, Object arg) {
			println("using facebook to login.\n");
		}
	};
	
	private static def gplus_login = new Work() {
		void eval(Job job, Object arg) {
			println("using google+ to login.\n");
		}
	};
	
	private static def openid_login = new Work() {
		void eval(Job job, Object arg) {
			println("using open-id to login.\n");
		}
	};
	
	private static def db_login = new Work() {
		void eval(Job job, Object arg) {
			println("using internal db to login.\n");
		}
	};
	
}

