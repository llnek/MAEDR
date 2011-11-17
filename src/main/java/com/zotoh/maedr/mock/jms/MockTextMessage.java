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

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Random;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author kenl
 *
 */
public class MockTextMessage implements TextMessage {

    private String _text,
    _type="Mock-Text-Message";
    
    /**
     * @param txt
     */
    public MockTextMessage(String txt) {
        _text=txt;
    }
    
    @Override
    public void acknowledge() throws JMSException {
        
    }

    @Override
    public void clearBody() throws JMSException {
        
    }

    @Override
    public void clearProperties() throws JMSException {
        
    }

    @Override
    public boolean getBooleanProperty(String arg0) throws JMSException {
        return false;
    }

    @Override
    public byte getByteProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public double getDoubleProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public float getFloatProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public int getIntProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return Integer.toString( new Random().nextInt(Integer.MAX_VALUE));
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        try {
            return Integer.toString( new Random().nextInt(Integer.MAX_VALUE)).getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e)        {
            throw new JMSException(e.getMessage());
        }
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return 0;
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return null;
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return 0;
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return "msg-" + new Random().nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return 0;
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return false;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return null;
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return 0;
    }

    @Override
    public String getJMSType() throws JMSException {
        return _type;
    }

    @Override
    public long getLongProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public Object getObjectProperty(String arg0) throws JMSException {
        return null;
    }

    @Override
    public Enumeration<?> getPropertyNames() throws JMSException {
        return null;
    }

    @Override
    public short getShortProperty(String arg0) throws JMSException {
        return 0;
    }

    @Override
    public String getStringProperty(String arg0) throws JMSException {
        return null;
    }

    @Override
    public boolean propertyExists(String arg0) throws JMSException {
        return false;
    }

    @Override
    public void setBooleanProperty(String arg0, boolean arg1)
            throws JMSException {
        
    }

    @Override
    public void setByteProperty(String arg0, byte arg1) throws JMSException {
        
    }

    @Override
    public void setDoubleProperty(String arg0, double arg1) throws JMSException {
        
    }

    @Override
    public void setFloatProperty(String arg0, float arg1) throws JMSException {
        
    }

    @Override
    public void setIntProperty(String arg0, int arg1) throws JMSException {
        
    }

    @Override
    public void setJMSCorrelationID(String arg0) throws JMSException {
        
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
        
    }

    @Override
    public void setJMSDeliveryMode(int arg0) throws JMSException {
        
    }

    @Override
    public void setJMSDestination(Destination arg0) throws JMSException {
        
    }

    @Override
    public void setJMSExpiration(long arg0) throws JMSException {
        
    }

    @Override
    public void setJMSMessageID(String arg0) throws JMSException {
        
    }

    @Override
    public void setJMSPriority(int arg0) throws JMSException {
        
    }

    @Override
    public void setJMSRedelivered(boolean arg0) throws JMSException {
        
    }

    @Override
    public void setJMSReplyTo(Destination arg0) throws JMSException {
        
    }

    @Override
    public void setJMSTimestamp(long arg0) throws JMSException {
        
    }

    @Override
    public void setJMSType(String arg0) throws JMSException {
        _type=arg0;
    }

    @Override
    public void setLongProperty(String arg0, long arg1) throws JMSException {
        
    }

    @Override
    public void setObjectProperty(String arg0, Object arg1) throws JMSException {
        
    }

    @Override
    public void setShortProperty(String arg0, short arg1) throws JMSException {
        
    }

    @Override
    public void setStringProperty(String arg0, String arg1) throws JMSException {
        
    }

    @Override
    public String getText() throws JMSException {
        return _text;
    }

    @Override
    public void setText(String t) throws JMSException {
        _text=t;
    }
    
}
