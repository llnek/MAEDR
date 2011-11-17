/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.device;

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.util.Hashtable;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.GUID;

/**
 * A JMS client receiver.  The message is not confirmed by default unless an error occurs.  Therefore, the application is
 * responsible for the confirmation to messages.
 * 
 * The set of properties:
 * 
 * <b>contextfactory</b>
 * The class name of the context factory to be used as part of InitContext(). 
 * <b>connfactory</b>
 * The name of the connection factory.
 * <b>jndiuser</b>
 * The JNDI username, if any.
 * <b>jndipwd</b>
 * The JNDI user password, if any.
 * <b>jmsuser</b>
 * The username needed for your JMS server.
 * <b>jmspwd</b>
 * The password for your JMS server.
 * <b>durable</b>
 * Set to boolean true if message is persistent.
 * <b>providerurl</b>
 * The provider URL.
 * <b>destination</b>
 * The name of the destination.
 * 
 * @see com.zotoh.maedr.device.Device
 * 
 * @author kenl
 * 
 */
public class JmsIO extends Device  { 

    private String _JNDIPwd, _JNDIUser, _connFac
    ,_ctxFac, _url 
    ,_dest, _jmsUser, _jmsPwd;

    private Connection _conn;
    private boolean _durable;
    
    private void onMessage(Message msg)     {
        try   {
            dispatch(new JmsEvent(this, msg));
            msg=null;
        }
        catch (Exception e) {
            tlog().error("", e);
        }
        finally {
            if (msg!=null) try { msg.acknowledge(); } catch (Exception ee) {}        	
        }
    }
    
    /**
     * @param mgr
     */
    public JmsIO(DeviceManager<?,?> mgr) {
        super(mgr);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject deviceProperties) throws Exception {
        _ctxFac= trim( deviceProperties.optString("contextfactory"));
        _connFac= trim( deviceProperties.optString("connfactory"));
        _JNDIUser= trim( deviceProperties.optString("jndiuser"));
        _JNDIPwd= trim( deviceProperties.optString("jndipwd"));
        _jmsUser= trim( deviceProperties.optString("jmsuser"));
        _jmsPwd= trim( deviceProperties.optString("jmspwd"));
        _durable= deviceProperties.optBoolean("durable");
        _url= trim( deviceProperties.optString("providerurl"));
        _dest= trim( deviceProperties.optString("destination"));
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    protected void onStart() throws Exception {
        inizConn();
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    protected void onStop() {        
        try {
            if (_conn != null ) { _conn.close(); }
        }
        catch (Throwable t) {
            tlog().warn("",t);
        }
        _conn=null;
    }
    
    private void inizConn() throws Exception {
        
        Hashtable<String,String> vars= new Hashtable<String,String>();
        Context ctx;
        Object obj;

        if (!isEmpty(_ctxFac))
        {    vars.put(Context.INITIAL_CONTEXT_FACTORY, _ctxFac); }

        if (!isEmpty(_url))
        {    vars.put(Context.PROVIDER_URL, _url); }

        if (!isEmpty(_JNDIPwd))
        {    vars.put("jndi.password", _JNDIPwd); }
        
        if (!isEmpty(_JNDIUser))
        {    vars.put("jndi.user", _JNDIUser); }
                
        ctx= new InitialContext(vars);
        obj= ctx.lookup(_connFac);
        
        if (obj instanceof QueueConnectionFactory) {        
            inizQueue(ctx, obj);
        }
        else
        if (obj instanceof TopicConnectionFactory) {        
            inizTopic(ctx, obj);
        }
        else
        if (obj instanceof ConnectionFactory) {
            inizFac(ctx, obj);
        }
        else        {
            throw new Exception("JmsIO: unsupported JMS Connection Factory");
        }

        if (_conn != null) {   _conn.start();  }
    }

    private void inizFac(Context ctx, Object obj) throws Exception     {
        
        ConnectionFactory f= (ConnectionFactory) obj;
        final JmsIO me=this;
        Connection conn;
        Object c= ctx.lookup(_dest);

        if ( ! isEmpty(_jmsUser)) {
            conn= f.createConnection( _jmsUser, _jmsPwd);
        }  else {
            conn= f.createConnection();
        }
        
        _conn=conn;
        
        if (c instanceof Destination)   {
        	//TODO ? ack always ?
            Session s= conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer u= s.createConsumer( (Destination) c);
            u.setMessageListener(new MessageListener(){
				public void onMessage(Message msg) {
					me.onMessage(msg);
				}            	
            });
        }
        else        {
            throw new Exception("JmsIO: Object not of Destination type");
        }
    }
    
    private void inizTopic(Context ctx, Object obj) throws Exception     {
        
        TopicConnectionFactory f= (TopicConnectionFactory) obj;
        final JmsIO me=this;
        TopicConnection conn;
        Topic t= (Topic) ctx.lookup(_dest);

        if ( ! isEmpty(_jmsUser)) {
            conn= f.createTopicConnection(_jmsUser, _jmsPwd);
        } else {
            conn= f.createTopicConnection();
        }
        
        _conn=conn;

        TopicSession s= conn.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);
        TopicSubscriber b;

        if (_durable) {
            b= s.createDurableSubscriber(t, GUID.generate());
        }
        else {
            b= s.createSubscriber(t);
        }

        b.setMessageListener( new MessageListener(){
			public void onMessage(Message msg) {
				me.onMessage(msg);
			}        	
        });
    }
    
    private void inizQueue(Context ctx, Object obj) throws Exception     {
        
        QueueConnectionFactory f= (QueueConnectionFactory) obj;
        final JmsIO me=this;
        QueueConnection conn;
        Queue q= (Queue) ctx.lookup(_dest);

        if ( ! isEmpty(_jmsUser)) {
            conn= f.createQueueConnection(_jmsUser, _jmsPwd);
        } else {
            conn= f.createQueueConnection();
        }
        
        _conn=conn;
        
        QueueSession s= conn.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
        QueueReceiver r;
        
        r= s.createReceiver(q);
        r.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				me.onMessage(msg);
			}        	
        });
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {
        CmdLineQuestion q9= new CmdLineMandatory("dest", getResourceStr(rcb, "cmd.jms.dest")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("destination", answer);
                return "";
            }};
        CmdLineQuestion q8= new CmdLineMandatory("provurl", getResourceStr(rcb, "cmd.jms.purl")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("providerurl", answer);
                return "dest";
            }};
        CmdLineQuestion q7= new CmdLineQuestion("durable", getResourceStr(rcb, "cmd.jms.store"), "y/n","n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
            	boolean b= "Yy".indexOf(answer)>=0;
                props.put("durable", b);
                return "provurl";
            }};
        CmdLineQuestion q6= new CmdLineQuestion("jmspwd", getResourceStr(rcb, "cmd.jms.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("jmspwd", answer);
                return "durable";
            }};
        CmdLineQuestion q5= new CmdLineQuestion("jmsuser", getResourceStr(rcb,"cmd.jms.user")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("jmsuser", answer);
                return "jmspwd";
            }};
        CmdLineQuestion q4= new CmdLineQuestion("jndipwd", getResourceStr(rcb,"cmd.jndi.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("jndipwd", answer);
                return "jmsuser";
            }};
        CmdLineQuestion q3= new CmdLineQuestion("jndiuser", getResourceStr(rcb,"cmd.jndi.user")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("jndiuser", answer);
                return "jndipwd";
            }};
        CmdLineQuestion q2= new CmdLineMandatory("conn", getResourceStr(rcb,"cmd.jms.conn")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("connfactory", answer);
                return "jndiuser";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("ctx", getResourceStr(rcb,"cmd.jms.ctx")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("contextfactory", answer);
                return "conn";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1,q2,q3,q4,q5,q6,q7,q8,q9){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    
    
}
