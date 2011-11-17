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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;


/**
 * @author kenl
 *
 */
public class MockQueueReceiver implements QueueReceiver {

    private Queue _queue;
    
    /**
     * @param q
     */
    public MockQueueReceiver(Queue q)    {
        _queue=q;
    }
    
    @Override
    public void close() throws JMSException {
        _queue= null;
        _sub=null;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return _sub;
    }

    @Override
    public String getMessageSelector() throws JMSException {
        return null;
    }

    @Override
    public Message receive() throws JMSException {
        return null;
    }

    @Override
    public Message receive(long arg0) throws JMSException {
        return null;
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        return null;
    }

    @Override
    public void setMessageListener(MessageListener sub) throws JMSException {
        _sub=sub;
    }

    @Override
    public Queue getQueue() throws JMSException {
        return _queue;
    }
    
    private MessageListener _sub;
}