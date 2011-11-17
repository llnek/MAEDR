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

package com.zotoh.maedr.mock.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * @author kenl
 *
 */
public class MockQueueConnection implements QueueConnection {
    
    private volatile boolean _active=false;
    
    /**
     * @param user
     * @param pwd
     */
    public MockQueueConnection(String user, String pwd)    {
        this(); 
    }
    
    /**
     * 
     */
    public MockQueueConnection()    {
        _active=true;
    }
    
    @Override
    public void close() throws JMSException {
        stop();
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination arg0,
            String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
        return null;
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
            String arg1, String arg2, ServerSessionPool arg3, int arg4)
            throws JMSException {
        return null;
    }

    @Override
    public Session createSession(boolean arg0, int arg1) throws JMSException {
        return null;
    }

    @Override
    public String getClientID() throws JMSException {
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    @Override
    public void setClientID(String arg0) throws JMSException {
        
    }

    @Override
    public void setExceptionListener(ExceptionListener arg0)
            throws JMSException {
        
    }

    @Override
    public void start() throws JMSException {
        _active=true;
    }

    @Override
    public void stop() throws JMSException {
        _active=false;
    }

    /**
     * @return
     */
    public boolean isActive() { return _active; }
    
    @Override
    public ConnectionConsumer createConnectionConsumer(Queue arg0, String arg1,
            ServerSessionPool arg2, int arg3) throws JMSException {
        return null;
    }

    @Override
    public QueueSession createQueueSession(boolean transact, int ack)
            throws JMSException {
        MockQueueSession s= new MockQueueSession(this, transact, ack);
        s.run();
        return s;
    }
    
}
