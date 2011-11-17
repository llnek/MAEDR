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

import static com.zotoh.core.util.LangUte.MP;

import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.zotoh.core.util.ProcessUte;

/**
 * @author kenl
 *
 */
public class MockTopicSession implements TopicSession {

    private Map<String,TopicSubscriber> _subs= MP();
    private MockTopicConnection _conn;
    private volatile boolean _active;
    
    /**
     * @param c
     * @param transact
     * @param ack
     */
    public MockTopicSession(MockTopicConnection c, boolean transact, int ack) {
        _conn=c;
    }
    
    @Override
    public void close() throws JMSException {
        _active=false;
    }

    @Override
    public void commit() throws JMSException {
        
    }

    @Override
    public QueueBrowser createBrowser(Queue arg0) throws JMSException {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue arg0, String arg1)
            throws JMSException {
        return null;
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination t) throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination t, String arg1)
            throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination t, String arg1,
            boolean arg2) throws JMSException {
        return null;
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return null;
    }

    @Override
    public Message createMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable arg0)
            throws JMSException {
        return null;
    }

    @Override
    public MessageProducer createProducer(Destination arg0) throws JMSException {
        return null;
    }

    @Override
    public Queue createQueue(String arg0) throws JMSException {
        return null;
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return null;
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null;
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return createTextMessage("");
    }

    @Override
    public TextMessage createTextMessage(String txt) throws JMSException {
        return new MockTextMessage(txt);
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return 0;
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return false;
    }

    @Override
    public void recover() throws JMSException {
        
    }

    @Override
    public void rollback() throws JMSException {
        
    }

    @Override
    public void run() {
        _active=true;
        
        final MockTopicSession me=this;
        Thread t= new Thread(new Runnable() {
            public void run() {
                ProcessUte.safeThreadWait(3000);
                while (me._active && me._conn.isActive())
                try {
                    me.trigger();
                    Thread.sleep(3000);
                } catch (Throwable t) {}
                me._active=false;
            }         
        });
        t.setDaemon(true);
        t.start();
    }

    private void trigger() throws Exception {
        TextMessage m= createTextMessage(MockUte.makeNewTextMsg_x() );
        m.setJMSType("Mock-Topic-Type");        
        MessageListener ml;
        for (TopicSubscriber s : _subs.values() ) {
            ml=s.getMessageListener();
            if (ml != null) {
                ml.onMessage(m);
            }
        }
    }
    
    @Override
    public void setMessageListener(MessageListener arg0) throws JMSException {
        
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic t, String name)
            throws JMSException {
        MockTopicSubscriber s= new MockTopicSubscriber(t, name);
        _subs.put(t.getTopicName(), s);
        return s;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic t, String name,
            String arg2, boolean arg3) throws JMSException {
        return null;
    }

    @Override
    public TopicPublisher createPublisher(Topic t) throws JMSException {
        return null;
    }

    @Override
    public TopicSubscriber createSubscriber(Topic t) throws JMSException {
        MockTopicSubscriber s= new MockTopicSubscriber(t);
        _subs.put(t.getTopicName(), s);
        return s;
    }

    @Override
    public TopicSubscriber createSubscriber(Topic arg0, String arg1,
            boolean arg2) throws JMSException {
        return null;
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;
    }

    @Override
    public Topic createTopic(String arg0) throws JMSException {
        return null;
    }

    @Override
    public void unsubscribe(String arg0) throws JMSException {
        
    }
    
}
