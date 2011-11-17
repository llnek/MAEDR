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

import static com.zotoh.netio.NetUte.*;
import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstNonNegIntArg;
import static com.zotoh.core.util.ProcessUte.asyncExec;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.ResourceBundle;

import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.netio.NetUte;

/**
 * A general TCP socket device.
 * 
 * The set of properties:
 * 
 * <b>host</b>
 * The hostname where this device is running on, default is localhost.
 * <b>port</b>
 * The port no.
 * <b>soctoutmillis</b>
 * The socket time out in milliseconds, default is 0.
 * <b>backlog</b>
 * The tcp backlog, default is 100.
 * <b>binary</b>
 * Set to boolean true if data is to be treated as binary.
 * <b>encoding</b>
 * Set to character encoding if the data is text, default is utf-8.
 * 
 * 
 * @see com.zotoh.maedr.device.Device
 * 
 * @author kenl
 * 
 */
public class TcpIO extends Device  {
    
    private int _port, _backlog, _socTOutMillis;
    private boolean _binary;
    private String _host, _encoding;
    private ServerSocket _ssoc;
    
    /**
     * @param mgr
     */
    public TcpIO(DeviceManager<?,?> mgr) {
        super(mgr);
    }
    
    /**
     * @return
     */
    public String getHost() { return _host; }
        
    /**
     * @return
     */
    public int getPort() { return _port;}
    
    /**
     * @return
     */
    public boolean isBinary() { return _binary; }
    
    /**
     * @return
     */
    public String getEncoding() { return _encoding; }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject deviceProperties) 
            throws Exception {        
        int socto= deviceProperties.optInt("soctoutmillis", 0);
        String host= trim(deviceProperties.optString("host"));
        int port= deviceProperties.optInt("port",-1);
        String enc= trim( deviceProperties.optString("encoding") );
        boolean bin= deviceProperties.optBoolean("binary");
        int blog= deviceProperties.optInt("backlog",100);
        
        if (isEmpty(host)) {            host="localhost" ;               }
        if (isEmpty(enc)) {            enc= "UTF-8" ;        }
    
        _encoding = enc;
        _binary = bin;
        _host = host;
        
        tstNonNegIntArg("tcp-port", port) ;
        _port= port;
        
        tstNonNegIntArg("tcp-backlog", blog) ;
        _backlog= blog;
        
        tstNonNegIntArg("socket-timeout-millis", socto) ;
        _socTOutMillis= socto;
        
    }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    protected void onStart() throws Exception {
        
        final TcpIO me=this;
        
        me._ssoc= createSvrSockIt();
        
        asyncExec( new Runnable() {
            public void run() {
                while ( me._ssoc != null)
                try {
                    me.sockItDown( me._ssoc.accept() );
                }
                catch (Exception e) {
                   // tlog().warn("", e) ;
                    me.closeSoc();
                }
            }
        });
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    protected void onStop() {        closeSoc();    }
    
    private void closeSoc() {
        _ssoc= NetUte.close(_ssoc) ;        
    }
    
    private void sockItDown(Socket s) throws Exception    {
        
        TCPEvent ev= new TCPEvent(this, s);

        ev.setSocketTimeout(_socTOutMillis) ;
        ev.setEncoding(_encoding);
        ev.setBinary(_binary);

        dispatch(ev);
    }

    private ServerSocket createSvrSockIt() throws IOException     {
        
        InetAddress ip;        
        ip= isEmpty(_host) ? InetAddress.getLocalHost() : InetAddress.getByName(_host) ;
        
        ServerSocket soc= new ServerSocket(_port, _backlog, ip); 
        ServerSocket s=null;
        try         {
            soc.setReuseAddress(true);
            s=soc;
            soc=null;
        }
        finally {
            NetUte.close(soc);
        }

        tlog().debug("TCP: opened server socket: {} on host: {}" , _port , _host);
        return s;
    }
            
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    @Override
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception { 
        props.put("backlog", 100);
        props.put("soctoutmillis",0);
        props.put("binary", true);
        props.put("encoding", "utf-8");
        CmdLineQuestion q2= new CmdLineMandatory("port", getResourceStr(rcb,"cmd.port")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("port", asInt(answer, -1));
                return "";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("host", getResourceStr(rcb, "cmd.host"), "",getLocalHost()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("host", answer);
                return "port";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1,q2){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
}
