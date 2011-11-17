/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.test.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;
import com.zotoh.crypto.Crypto;
import com.zotoh.maedr.device.BaseHttpIO;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.FilePicker;

import com.zotoh.maedr.device.netty.NettpIO;
import com.zotoh.maedr.device.PopIO;
import com.zotoh.maedr.process.ProcBaseEngine;

/**/
public class JUT {
    
	private ProcBaseEngine _eng;
	private static File _p12File;
	private static String _p12Path;
	
    /**/
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUT.class);
    }

    /**/
    //@BeforeClass
    public static void iniz() throws Exception    {    	    	       
    	
    	File out= StreamUte.createTempFile();
        long now= new Date().getTime();
        long m3= 1000L*60*60*24*90; // 3mths
        Date start= new Date(now-m3) ;
        Date end= new Date(now+m3) ;
    	Crypto.getInstance().createSSV1PKCS12("test", 
    			start, end, 
				"C=AU,ST=NSW,L=Sydney,O=Test (Sample Only),CN=www.yoyoma.com", 
				"secret", 1024, out);
    	_p12File=out;
    	_p12Path= ( CoreUte.isWindows() ? "file:///" : "file://" ) + CoreUte.niceFPath(out);
    }    

    /**/
    @AfterClass
    public static void finz()    {
    	if ( _p12File != null) _p12File.delete();
    }    

    /**/
    @Before
    public void open() throws Exception    {
    	_eng= new ProcBaseEngine();
    }    

    /**/
    @After
    public void close() throws Exception    {        
    }

    
    //@Test
    public void testApp() throws Exception {
        File t= StreamUte.createTempFile();
        String s= "{ devices: { d1: { type:\"rest\", port:8080,context:\"/storefront\", resources:[{ path:\"/account/list\", processor:\"samples.rest.ListAcctsProcessor\"},{ path:\"/account/[1-9][0-9]+\", processor:\"samples.rest.UserAcctProcessor\"},{ path:\"/cart/[1-9][0-9]+\", processor:\"samples.rest.ShopCartProcessor\"}] }                                     }}}";
//        String s= "{ devices: { d1: { type:\"jetty\", port:8080, resbase:\"file:/w:/local/x/samples/jetty/webapps\", contextpath:\"/test\", urlpatterns:[\"/helloworld/*\"] }                                     }}}";
//        String s= "{ devices: { d1: { type:\"oneshot-timer\", delaysecs:3 }                                     }}}";
//        String s= "{ devices: { d1: { type:\"pop3\", port:7110, host:\"localhost\", intervalsecs:5, delaysecs:3, user:\"a\", pwd:\"a\", provider:\"com.zotoh.maedr.mock.mail.MockPop3Store\" }                                     }}}";
        StreamUte.writeFile(t,s,"utf-8");
        ProcBaseEngine app= new ProcBaseEngine();
        Properties props= new Properties();
//        props.put("maedr.delegate.class", "samples.jetty.JettyDelegate");
//        props.put("maedr.delegate.class", "com.zotoh.maedr.test.TestDelegate");
        props.put("maedr.manifest.file", CoreUte.niceFPath(t));
        
        props.put("maedr.shutdown.port.password","stopengine");
        props.put("maedr.shutdown.port","7051");
        props.put("maedr.nio.choice","apache");

        app.start(props);
        t.delete();
        
        assertFalse(t.exists());
    }
    
    //@Test
    public void testCreateFPicker() throws Exception {
        DeviceManager<?,?> dm= _eng.getDeviceManager();
        JSONObject props= new JSONObject();
        FilePicker io= new FilePicker(dm);
        File r=CoreUte.genTmpDir();
        File d=CoreUte.genTmpDir();
        props.put("id", "a");
        props.put("delaysecs", 60);
        props.put("intervalsecs", 30);
        props.put("rootdir", r.getCanonicalPath());
        props.put("destdir", d.getCanonicalPath());
        props.put("automove", true);
        props.put("fmask", ".*.txt");
        io.configure(props);
        io.start();
        assertTrue(io.isActive());
        assertTrue(io.isEnabled());
        Thread.sleep(1500);
        io.stop();
        assertFalse(io.isActive());
        assertTrue(io.isEnabled());
    }
    
    //@Test
    public void testCreatePOP3() throws Exception {
        DeviceManager<?,?> dm= _eng.getDeviceManager();
        JSONObject props= new JSONObject();
        PopIO io = new PopIO(dm);
        props.put("id", "a");       
        props.put("host", "localhost");
        props.put("port", 9000);
        props.put("user", "test");
        props.put("pwd", "secret");
        props.put("provider", "com.zotoh.maedr.mock.mail.MockPop3Store");
        io.configure(props);
        io.start();
        assertTrue(io.isActive());
        assertTrue(io.isEnabled());
        Thread.sleep(1500);
        io.stop();
        assertFalse(io.isActive());
        assertTrue(io.isEnabled());
    }

    
    
    
    
    //@Test
    public void testCreateHttp() throws Exception {
    	t_createHttp(false,false);
    	t_createHttp(false,true);
    	t_createHttp(true, false);
    	t_createHttp(true, true);
    }
    private void t_createHttp(boolean netty, boolean async) throws Exception {
    	DeviceManager<?,?> dm= _eng.getDeviceManager();
    	JSONObject props= new JSONObject();
    	props.put("id", "a");
    	props.put("host", "localhost");
    	props.put("port", 8080);
    	props.put("async", async);
    	props.put("workers", 2);
    	props.put("soctoutmillis", 5000);
    	props.put("thresholdkb", 100);
    	props.put("waitmillis", 3000);
    	BaseHttpIO io;
        io=new NettpIO(dm);
    	io.configure(props);
    	io.start();    	
    	Thread.sleep(1500);
    	assertTrue(io.isEnabled());
    	assertTrue(io.isActive());
    	if (netty) {
    		assertTrue(io.isAsync());
    	}
    	else {
    		assertTrue(io.isAsync()==async);
    	}
    	assertTrue(io.getPort() == 8080);
    	assertTrue("localhost".equals(io.getHost()));
    	assertTrue(io.getSocetTimeoutMills() == 5000);
    	assertTrue("a".equals(io.getId()));
    	assertTrue( io.getThreshold() == 100*1024);
    	assertTrue(io.getWaitMillis() == 3000);
    	assertTrue(io.getWorkers()==2);
    	assertFalse(io.isSSL());
    	io.stop();
    	Thread.sleep(1500);
    	assertFalse(io.isActive());
    	assertTrue(io.isEnabled());
    }
    
    //@Test
    public void testCreateHttpSSL() throws Exception {    
    	String[] types= new String[]{ "SSLv3", "TLS"};
    	for (int i=0; i < types.length; ++i) {    		
    		t_createHttpSSL(types[i], false, false);
    		t_createHttpSSL(types[i], false, true);
    	}
    	for (int i=0; i < types.length; ++i) {    		
    		t_createHttpSSL(types[i], true, false);
    		t_createHttpSSL(types[i], true, true);
    	}    	
    }
    private void t_createHttpSSL(String flavor, boolean netty, boolean async) throws Exception {
    	DeviceManager<?,?> dm= _eng.getDeviceManager();
    	JSONObject props= new JSONObject();
    	props.put("id", "a");
    	props.put("host", "localhost");
    	props.put("port", 9090);
    	props.put("flavor", flavor);    	
    	props.put("async", async);
    	props.put("serverkeypwd", "secret");
    	props.put("serverkey", _p12Path);
    	BaseHttpIO io=new NettpIO(dm, true);
    	io.configure(props);
    	io.start();    	
    	Thread.sleep(1500);
    	assertTrue(io.isEnabled());
    	assertTrue(io.isActive());
    	assertTrue(io.getPort() == 9090);
    	assertTrue(flavor.equals(io.getSSLType()));
    	assertTrue("localhost".equals(io.getHost()));
    	assertTrue("a".equals(io.getId()));
    	assertTrue( "secret".equals( io.getKeyPwd()));
    	assertTrue(io.isSSL());
    	io.stop();
    	Thread.sleep(1500);
    	assertFalse(io.isActive());
    	assertTrue(io.isEnabled());
    }

    @Test
    public void testDummy() throws Exception {}
    
}
