/*??
 * COPYRIGHT (C) 2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

import static com.zotoh.core.util.CoreUte.asFileUrl;
import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstNonNegIntArg;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;
import static com.zotoh.netio.NetUte.getLocalHost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;

import org.json.JSONObject;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Tuple;
import com.zotoh.crypto.Crypto;
import com.zotoh.crypto.CryptoStore;
import com.zotoh.crypto.JKSStore;
import com.zotoh.crypto.PKCSStore;

/**
 * Base class to all HTTP oriented devices.
 * 
 * The set of properties:
 * 
 * <b>host</b>
 * The hostname to run on - default is localhost.
 * <b>port</b>
 * The port to run on.
 * <b>serverkey</b>
 * The full path pointing to the server key file (p12 or jks) file.  If this value is set, SSL is assumed.
 * <b>serverkeypwd</b>
 * The password for the key file.
 * 
 * @see com.zotoh.maedr.device.Device
 * 
 * @author kenl
 * 
 */
public abstract class HttpIOTrait extends Device {

	private String _keyPwd, _host, _sslType;
	private URL _keyURL;
	private int _port;
	private boolean _secure;
	
	/**
	 * @param mgr
	 * @param ssl
	 */
	protected HttpIOTrait(DeviceManager<?,?> mgr, boolean ssl) {
		super(mgr);
		_secure=ssl;
	}
	
	/**
	 * @param mgr
	 */
	protected HttpIOTrait(DeviceManager<?,?> mgr) {
		this(mgr,false);
	}
	
	/**
	 * @return
	 */
	public boolean isSSL() { return _secure; }
	
	/**
	 * @return
	 */
	public int getPort() { return _port; }
	
	/**
	 * @return
	 */
	public String getHost() { return _host; }
	
	/**
	 * @return
	 */
	public String getSSLType() { return _sslType; }
	
	/**
	 * @return
	 */
	public URL getKeyURL() { return _keyURL; }
	
	/**
	 * @return
	 */
	public String getKeyPwd() { return _keyPwd; }
	
    /**
     * @return
     * @throws UnknownHostException
     */
    public InetAddress getIP() throws UnknownHostException {
        return isEmpty(_host) ? InetAddress.getLocalHost()  
            : InetAddress.getByName(_host) ;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject deviceProperties) throws Exception {
        
        String host = trim(deviceProperties.optString("host") );
        int port= deviceProperties.optInt("port",-1) ;
        String pwd= trim(deviceProperties.optString("serverkeypwd") );
        String key= trim(deviceProperties.optString("serverkey") );
        String sslType= trim(deviceProperties.optString("flavor") );

        tstNonNegIntArg("port", port) ;
        
        if (_secure && isEmpty(key)) {
            tstEStrArg("ssl-key-file", key) ;            
        }
        
        if (isEmpty(sslType)) {
            sslType= "TLS" ;
        }
        
        if (isEmpty(host)) {
            host= "" ;
        }
    	
        _sslType = sslType;
        _port = port;
        _host= host;
        
        if ( !isEmpty(key)) {
            tstEStrArg("ssl-key-file-password", pwd) ;
            _keyURL = new URL( key.startsWith("file:") ? key : asFileUrl(new File(key)) ) ;
            _keyPwd= PwdFactory.getInstance().create(pwd).getAsClearText();
            _secure=true;
        }
                
    }    
	
    /**
     * @param createContext
     * @param sslType
     * @param key
     * @param pwd
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     */
    protected static Tuple cfgSSL(boolean createContext, String sslType, URL key, String pwd) 
    throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, 
    CertificateException, IOException, KeyManagementException {

        boolean jks = key.getFile().endsWith(".jks");
        InputStream inp= key.openStream();
        CryptoStore s;
        
        try {
        	s= jks ? new JKSStore() : new PKCSStore();
            s.init(pwd ) ;            
            s.addKeyEntity(inp, pwd );            
        }
        finally {
            StreamUte.close(inp);
        }
        
        SSLContext c = null;
        if (createContext) {
            c = SSLContext.getInstance( sslType );
            c.init( s.getKeyManagerFactory().getKeyManagers(),
                    s.getTrustManagerFactory().getTrustManagers(),
                    Crypto.getInstance().getSecureRandom() );                 
        }
        
        return new Tuple(s, c);
    }
          
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.device.Device#supportsConfigMenu()
	 */
	public boolean supportsConfigMenu() { return true; }
	
	/* (non-Javadoc)
	 * @see com.zotoh.maedr.device.Device#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
	 */
	protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) throws Exception {	    
	    props.put("soctoutmillis", 0);
	    
        CmdLineQuestion q7= new CmdLineQuestion("wtds", getResourceStr(rcb,"cmd.work.thds"), "","8") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("workers", asInt(answer,8));
                return "";
            }};           
        CmdLineQuestion q6= new CmdLineQuestion("wait", getResourceStr(rcb,"cmd.async.wait"), "","300") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("waitmillis", asInt(answer,300)*1000);
                return "wtds";
            }};            
        CmdLineQuestion q5= new CmdLineMandatory("keypwd", getResourceStr(rcb,"cmd.key.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("serverkeypwd", answer);
                return "wait";
            }};
        CmdLineQuestion q4= new CmdLineMandatory("keyfile", getResourceStr(rcb, "cmd.key.file")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("serverkey", answer);
                return "keypwd";
            }};           
        CmdLineQuestion q3= new CmdLineQuestion("ssl", getResourceStr(rcb, "cmd.use.ssl"), "y/n","n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                boolean b= "Yy".indexOf(answer)>=0;
                return b? "keyfile" : "wait";
            }};           
        CmdLineQuestion q2= new CmdLineQuestion("port", getResourceStr(rcb,"cmd.port"), "","8080") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("port", asInt(answer,8080));
                return "ssl";
            }};           
        final CmdLineQuestion q1= new CmdLineQuestion("host", getResourceStr(rcb, "cmd.host"), "", getLocalHost()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("host", answer);
                return "port";
            }};
                        
        return new CmdLineSequence(super.getCmdSeq(rcb, props) ,q1,q2,q3,q4,q5,q6,q7){
            protected String onStart() {
                return q1.getId();
            }           
        };
	}
	
}
