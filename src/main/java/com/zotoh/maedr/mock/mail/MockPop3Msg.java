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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;

/**
 * @author kenl
 *
 */
public class MockPop3Msg extends MimeMessage {

	private String _msg= "hello joe!";
	
	/**
	 * @param folder
	 * @param msgnum
	 */
	public MockPop3Msg(Folder folder, int msgnum) {
		super(folder, msgnum);
	}

	@Override
	public Enumeration<?> getAllHeaderLines() throws MessagingException {
		Vector<String> lst= new Vector<String>();
		byte[] bits=null; 
		_msg = "The current time is: " + CoreUte.fmtDate(new Date());
		try { bits=_msg.getBytes("utf-8"); } catch (Exception e) {}
		lst.add("message-id: a-mock-pop3-msg-"+new Random().nextInt(1000));
		lst.add("from: mickey@koala.com");
        lst.add("to: anonymous@acme.com");
        lst.add("subject: hello world");
		lst.add("content-length: " + bits.length);
		return lst.elements();
	}

	@Override
	public InputStream getRawInputStream() throws MessagingException {
		try { return StreamUte.asStream( _msg.getBytes("utf-8")) ; } catch (Exception e) {}
		return null;
	}
	
	@Override
	public InputStream getInputStream() throws IOException, MessagingException {
	    return getRawInputStream();
	}
}


