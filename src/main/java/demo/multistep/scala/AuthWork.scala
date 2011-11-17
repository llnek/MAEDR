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


object AuthWork {
	
	def getAuthMtd(t:String) : PTask = {
		
        t match {
          case "facebook" => new PTask(facebook_login)
          case "google+" => new PTask(gplus_login)
          case "openid" => new PTask(openid_login)
          case _ => new PTask(db_login)
        }
		
	}
	
	private val facebook_login = new Work() {
		def eval(job:Job , arg:Object ) {
			println("using facebook to login.\n")
		}
    }
	
	private val gplus_login = new Work() {
		def eval(job:Job, arg:Object) {
			println("using google+ to login.\n")
		}
	}
	
	private val openid_login = new Work() {
		def eval(job:Job, arg:Object) {
			println("using open-id to login.\n")
		}
	}
	
	private val db_login = new Work() {
		def eval(job:Job, arg:Object) {
			println("using internal db to login.\n")
		}
	}
	
}

