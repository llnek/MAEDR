/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;


/**
 * @author kenl
 *
 */
public class MockPop3Store extends Store {

	protected String name = "pop3";
	protected int defaultPort = 110;

	protected boolean isSSL=false;

	protected int portNum = -1;
	protected String host=null, user = null,
	passwd = null;

	/**
	 * @param session
	 * @param url
	 */
	public MockPop3Store(Session session, URLName url) {
		this(session, url, "pop3");
	}
    
	/**
	 * @param session
	 * @param url
	 * @param name
	 */
	public MockPop3Store(Session session, URLName url, String name) {
		super(session, url);

		if (url != null)
			name = url.getProtocol();

		this.defaultPort = 110;
		this.name = name;
	}

	protected synchronized boolean protocolConnect(String host, int portNum,
					String user, String passwd) throws MessagingException {
		
		if ((host == null) || (passwd == null) || (user == null)) {
			return false;
		}
		
		if (portNum == -1) {
			portNum = this.defaultPort;
		}
		
		this.host = host;
		this.portNum = portNum;
		this.user = user;
		this.passwd = passwd;
		
		return true;
	}

	public synchronized boolean isConnected() {
		if (!super.isConnected()) {
			return false;
		}
		return true;
	}

	public synchronized void close() throws MessagingException {
		super.close();
	}

	public Folder getDefaultFolder() throws MessagingException {
		checkConnected();
		return new DefaultFolder(this);
	}

	public Folder getFolder(String name) throws MessagingException {
		checkConnected();
		return new MockPop3Folder(this, name);
	}

	public Folder getFolder(URLName url) throws MessagingException {
		checkConnected();
		return new MockPop3Folder(this, url.getFile());
	}

	protected void finalize() throws Throwable {
		super.finalize();
	}

	private void checkConnected() throws MessagingException {
		if (!super.isConnected())
			throw new MessagingException("Not connected");
	}
}
