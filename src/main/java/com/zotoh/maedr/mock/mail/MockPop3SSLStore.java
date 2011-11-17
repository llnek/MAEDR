/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.mock.mail;

import javax.mail.Session;
import javax.mail.URLName;


/**
 * @author kenl
 *
 */
public class MockPop3SSLStore extends MockPop3Store {
	
	  /**
	 * @param session
	 * @param url
	 */
	public MockPop3SSLStore(Session session, URLName url) {
		    this(session, url, "pop3");
	}

	  /**
	 * @param session
	 * @param url
	 * @param name
	 */
	public MockPop3SSLStore(Session session, URLName url, String name)  {
		    super(session, url);
		    defaultPort = 995;
		    this.isSSL=true;
	}	
	
	  
	  
	  
	  
	  
}