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
import javax.jms.Topic;
import javax.jms.TopicSubscriber;


/**
 * @author kenl
 *
 */
public class MockTopicSubscriber implements TopicSubscriber {
    private MessageListener _sub;
    private Topic _topic;
    
//    private String _name;
    /**
     * @param t
     * @param name
     */
    public MockTopicSubscriber(Topic t, String name) {
        _topic=t;
        //_name=name;
    }
    
    /**
     * @param t
     */
    public MockTopicSubscriber(Topic t) {
        this(t, null);
    }
    
    @Override
    public void close() throws JMSException {
        _topic=null;
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
    public boolean getNoLocal() throws JMSException {
        return false;
    }

    @Override
    public Topic getTopic() throws JMSException {
        return _topic;
    }
    
}