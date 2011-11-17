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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;


/**
 * @author kenl
 *
 */
public class MockQueueConnFactory implements QueueConnectionFactory {

    /**
     * 
     */
    public MockQueueConnFactory()
    {}
    
    @Override
    public Connection createConnection() throws JMSException {
        return createQueueConnection();
    }

    @Override
    public Connection createConnection(String user, String pwd)
            throws JMSException {
        return createQueueConnection(user,pwd);
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return new MockQueueConnection();
    }

    @Override
    public QueueConnection createQueueConnection(String user, String pwd)
            throws JMSException {
        return new MockQueueConnection(user, pwd);
    }
    
}