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
 

package com.zotoh.maedr.device;


import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;


/**
 * A device acting as a POP3 client which pings the POP3 server periodically to check for new emails.
 * 
 * The set of properties:
 * 
 * <b>host</b>
 * The POP3 hostname.
 * <b>port</b>
 * The POP3 port, default is 110.
 * <b>ssl</b>
 * Set to boolean true if the connection to the POP3 server is secured, default is true.
 * <b>user</b>
 * The POP3 user login id.
 * <b>pwd</b>
 * The POP3 login password.
 * <b>deletemsg</b>
 * Set to boolean true is the message is marked as deleted, default is false.
 * 
 * @see com.zotoh.maedr.device.RepeatingTimer
 * 
 * @author kenl
 * 
 */
public class PopIO extends ThreadedTimer {

//    private static final String ST_IMAP= "com.sun.mail.imap.IMAPStore" ; 
//    private static final String ST_IMAPS=  "com.sun.mail.imap.IMAPSSLStore";
    private static final String ST_POP3S=  "com.sun.mail.pop3.POP3SSLStore";
    private static final String ST_POP3=  "com.sun.mail.pop3.POP3Store";
//    private static final String CTL= "content-length" ;
    private static final String POP3S="pop3s";
    private static final String POP3="pop3";
    
    private String _user, _pwd, _host, _storeImpl;
    private int _port;
    private boolean _ssl, _delete;
    
    private Store _pop;
    private Folder _fd;
    
    /**
     * @param mgr
     */
    public PopIO(DeviceManager<?,?> mgr)     {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#inizWithProperties(org.json.JSONObject)
     */
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception     {        
        super.inizWithProperties(deviceProperties);

        String mock2= System.getProperties().getProperty("maedr.pop3.mockstore");
        String mock1= getDeviceManager().getEngine().getProperties()
        .getProperty("maedr.pop3.mockstore");
        
        //safePutProp( "provider", "com.zotoh.maedr.mock.mail.MockPop3Store");        
        //String impl = trim(deviceProperties.optString("provider") );
        String host= trim(deviceProperties.optString("host") );
        int port= deviceProperties.optInt("port", 110);
        
        _delete= deviceProperties.optBoolean("deletemsg", false);
        _ssl= deviceProperties.optBoolean("ssl", false);
        
        _user = trim(deviceProperties.optString("user") );
        _pwd= trim(deviceProperties.optString("pwd") );
        
        if (!isEmpty(_pwd)) {
        	_pwd= PwdFactory.getInstance().create(_pwd).getAsClearText();
        }
        
        tstPosIntArg("pop3-port", port) ;
        //tstEStrArg("host", host);        
        _host= host;
        _port=port;
        
        // this is really for testing only, points to a mock store
        if (!isEmpty(mock2)) {
            _storeImpl=mock2;        	
        } else if (!isEmpty(mock1)) {
        		_storeImpl=mock1;
        }
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#preLoop()
     */
    protected void preLoop() throws Exception {        
        _pop=null;
        _fd= null;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#endLoop()
     */
    @Override
    protected void endLoop()    {
        closePOP();
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#onOneLoop()
     */
    protected void onOneLoop() throws Exception {
        
        if ( conn() ) {
            try { 
                scanPOP(); 
            } 
            catch (Exception e) {
                tlog().warn("",e) ;
            }
            finally {
                closeFolder();
            }
        }
    }
    
    private void scanPOP() throws Exception {
        openFolder();
        getMsgs();
    }
    
    private void getMsgs() throws Exception     {
        
        int cnt= _fd.getMessageCount();

        tlog().debug("PopIO: count of new messages: {}" , cnt);        
        if (cnt <= 0)
        return;

        StringBuilder hds=new StringBuilder(512);
        Message[] msgs= _fd.getMessages();
        MimeMessage mm;
        StreamData data;
        String s;

        for (int i=0; i < msgs.length; ++i)        {
            mm=(MimeMessage)msgs[i];
            //TODO
            //_fd.getUID(mm);
            // read all the header lines
            hds.setLength(0);
            for (Enumeration<?> en= mm.getAllHeaderLines(); en.hasMoreElements();) {
                s= (String) en.nextElement();
//                if (s.toLowerCase().indexOf(CTL) >= 0) {}
//                else
                hds.append(s).append("\r\n");
            }
            
            data = StreamUte.readStream( mm.getRawInputStream());
            try {
                dispatch(new POP3Event(this, hds.toString(), data));
            }
            finally {
                if (_delete) { mm.setFlag(Flags.Flag.DELETED, true); }                
            }
        }
    }
    
    private boolean conn()     {
        
        if (_pop ==null || !_pop.isConnected())
        try  {
            Session session = Session.getInstance(new Properties(), null);
            Provider[] ps= session.getProviders();
            Provider sun=null;
            Store st=null;
            Folder f=null ;
            String uid= isEmpty(_user) ? null : _user;
            String pwd= isEmpty(_pwd) ? null : _pwd;
            String key= ST_POP3 ,
            sn= POP3;

            closePOP();
            
            if (_ssl) {
                key = ST_POP3S;
                sn= POP3S;
            }
            
            for(int i=0; i< ps.length; ++i) {                
                if (key.equals( ps[i].getClassName())) { 
                    sun=ps[i];
                    break;                    
                }
            }

            if ( ! isEmpty(_storeImpl)) {
            	// this should never happen , only in testing
                sun= new Provider(Provider.Type.STORE, "pop3", _storeImpl, "test", "1.0.0");
                sn=POP3;
            }
            
            session.setProvider(sun);
            st= session.getStore(sn);

            if ( st != null)  {                
                st.connect(_host, _port, uid , pwd);
                f= st.getDefaultFolder();
            }

            if (f != null) {
                f= f.getFolder("INBOX");
            }

            if (f==null || !f.exists()) {
                throw new Exception("POP3: Cannot find inbox");
            }

            _pop= st;
            _fd= f;
            
        }
        catch (Exception e) {
            tlog().warn("",e);
            closePOP();
        }
        
        return _pop != null && _pop.isConnected() ;
    }
    
    private void closePOP()    {
        
        closeFolder();
        try {            
            if (_pop != null) _pop.close();
        }
        catch (Exception e) {
            tlog().warn("", e);
        }
        _pop=null;
        _fd=null;
    }
    
    private void closeFolder()     {
        try         {
            if (_fd != null && _fd.isOpen()) _fd.close(true);
        }
        catch (Exception e) {
            tlog().warn("", e);
        }
    }
    
    private void openFolder() throws Exception     {
        if ( _fd != null && !_fd.isOpen()) {
            _fd.open(Folder.READ_WRITE);
        }
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {        
        CmdLineQuestion q6= new CmdLineQuestion("delmsg", getResourceStr(rcb, "cmd.pop3.delete"), "y/n", "n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("deletemsg", "Yy".indexOf(answer)>=0);
                return "";
            }};
        CmdLineQuestion q5= new CmdLineMandatory("pwd", getResourceStr(rcb, "cmd.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("pwd", answer);
                return "delmsg";
            }};
        CmdLineQuestion q4= new CmdLineMandatory("user", getResourceStr(rcb, "cmd.user")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("user", answer);
                return "pwd";
            }};
        CmdLineQuestion q3= new CmdLineQuestion("ssl", getResourceStr(rcb, "cmd.use.ssl"), "y/n","n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("ssl", "Yy".indexOf(answer)>=0);
                return "user";
            }};
        CmdLineQuestion q2= new CmdLineMandatory("port", getResourceStr(rcb, "cmd.pop3.port", "","110")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("port", asInt(answer, 110));
                return "ssl";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("host", getResourceStr(rcb, "cmd.pop3.host")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("host", answer);
                return "port";
            }};
        return new CmdLineSequence( super.getCmdSeq(rcb, props), q1,q2,q3,q4,q5,q6){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
}
