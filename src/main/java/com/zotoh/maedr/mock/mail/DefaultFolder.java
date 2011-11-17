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

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Store;

/**
 * @author kenl
 *
 */
public class DefaultFolder extends Folder {
	
  /**
 * @param store
 */
protected DefaultFolder(Store store)  {
    super(store);
  }

  public String getName() {
    return "";
  }

  public String getFullName() {
    return "";
  }

  public Folder getParent() {
    return null;
  }

  public boolean exists() {
    return true;
  }

  public Folder[] list(String pattern) throws MessagingException {
    Folder[] f = { getInbox() };
    return f;
  }

  public char getSeparator() {
    return '/';
  }

  public int getType() {
    return 2;
  }

  public boolean create(int type) throws MessagingException {
    return false;
  }

  public boolean hasNewMessages() throws MessagingException {
    return false;
  }

  public Folder getFolder(String name) throws MessagingException {
    if (!name.equalsIgnoreCase("INBOX")) {
      throw new MessagingException("only INBOX supported");
    }
    return getInbox();
  }

  protected Folder getInbox() throws MessagingException
  {
    return getStore().getFolder("INBOX");
  }

  public boolean delete(boolean recurse) throws MessagingException
  {
    throw new MethodNotSupportedException("delete");
  }

  public boolean renameTo(Folder f) throws MessagingException {
    throw new MethodNotSupportedException("renameTo");
  }

  public void open(int mode) throws MessagingException {
    throw new MethodNotSupportedException("open");
  }

  public void close(boolean expunge) throws MessagingException {
    throw new MethodNotSupportedException("close");
  }

  public boolean isOpen() {
    return false;
  }

  public Flags getPermanentFlags() {
    return new Flags();
  }

  public int getMessageCount() throws MessagingException {
    return 0;
  }

  public Message getMessage(int msgno) throws MessagingException {
    throw new MethodNotSupportedException("getMessage");
  }

  public void appendMessages(Message[] msgs) throws MessagingException {
    throw new MethodNotSupportedException("Append not supported");
  }

  public Message[] expunge() throws MessagingException {
    throw new MethodNotSupportedException("expunge");
  }
}
