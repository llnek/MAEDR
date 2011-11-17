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

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;


/**
 * @author kenl
 *
 */
public class MockPop3Folder extends Folder {

	private int _count=1;
	private String _name;
	
	/**
	 * @param store
	 * @param name
	 */
	public MockPop3Folder(Store store, String name) {
		super(store);
		_name=name;
	}

	@Override
	public void appendMessages(Message[] arg0) throws MessagingException {
	}

	@Override
	public void close(boolean arg0) throws MessagingException {
		_open=false;
	}

	@Override
	public boolean create(int arg0) throws MessagingException {
		return false;
	}

	@Override
	public boolean delete(boolean arg0) throws MessagingException {
		return false;
	}

	@Override
	public boolean exists() throws MessagingException {
		return true;
	}

	@Override
	public Message[] expunge() throws MessagingException {
		return null;
	}

	@Override
	public Folder getFolder(String arg0) throws MessagingException {
		return null;
	}

	@Override
	public String getFullName() {
		return _name;
	}

	@Override
	public Message getMessage(int pos) throws MessagingException {
		if (pos < 1) throw new MessagingException("wrong message num: " + pos);
		return new MockPop3Msg(this, pos);
	}

	@Override
	public int getMessageCount() throws MessagingException {
		return _count;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Folder getParent() throws MessagingException {
		return null;
	}

	@Override
	public Flags getPermanentFlags() {
		return null;
	}

	@Override
	public char getSeparator() throws MessagingException {
		return 0;
	}

	@Override
	public int getType() throws MessagingException {
		return 0;
	}

	@Override
	public boolean hasNewMessages() throws MessagingException {
		return false; // must be false
	}

	@Override
	public boolean isOpen() {
		return _open;
	}

	@Override
	public Folder[] list(String arg0) throws MessagingException {
		return null;
	}

	@Override
	public void open(int arg0) throws MessagingException {
		_open=true;
	}

	@Override
	public boolean renameTo(Folder arg0) throws MessagingException {
		return false;
	}

	private boolean _open;
	
}