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

package com.zotoh.maedr.test.flow;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.wflow.FlowBaseEngine;

/**/
public class BaseJUT implements Vars {
    
	private static File _appDir, _p12File;
//	private static String _p12Path;
	protected FlowBaseEngine _eng;
	protected Object _lock= new Object();
	
    /**/
    @BeforeClass
    public static void iniz() throws Exception    {    	    
        System.setProperty(PIPLINE_MODULE, "com.zotoh.maedr.wflow.FlowModule");        
        _appDir=CoreUte.genTmpDir();
        new File(_appDir, LOGS).mkdirs();
        new File(_appDir, CFG).mkdirs();
    }    

    /**/
    @AfterClass
    public static void finz()    {
    	if ( _p12File != null) _p12File.delete();
    	if (_appDir != null) try {
    	    FileUtils.cleanDirectory(_appDir);
    	    FileUtils.deleteQuietly(_appDir);
    	} catch (Exception e) {}
    }    

    protected Properties create_props(String flow) throws Exception {
        File t= new File(new File(_appDir,CFG), "app.conf");
        String s= deviceBlock(flow);
        StreamUte.writeFile(t,s,"utf-8");
        Properties props= new Properties();
        props.put("maedr.manifest.file", CoreUte.niceFPath(t));        
        props.put("maedr.shutdown.port.password","stopengine");
        props.put("maedr.shutdown.port","7051");
        props.put(APP_DIR, CoreUte.niceFPath(_appDir));        
        return props;
    }
    
    protected String deviceBlock(String flow) {
        return "{ devices : {"
                        + "h1 : {"
                        + "processor:\"" + flow + "\","
                        + "type:\"oneshot-timer\","
                        + "delaysecs:1"
                + "}}}";    	
    }
    
    /**/
    @Before
    public void open() throws Exception    {
    		_eng= new FlowBaseEngine();
    		onOpen();
    }    

    protected void onOpen() throws Exception {}
    
    /**/
    @After
    public void close() throws Exception    {
        _eng.shutdown();
    }

    protected void block() {
		synchronized(_lock) {
			try {
				_lock.wait();
			} catch (Exception e) {}
		}
    }
    
    protected void wake() {
    		synchronized(_lock) {
    			try {
    				_lock.notify();
    			} catch (Exception e) {}
    		}
    }
    
    
}
