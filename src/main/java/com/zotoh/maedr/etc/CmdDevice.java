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
 
package com.zotoh.maedr.etc;

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.maedr.util.MiscUte.existsDevice;
import static com.zotoh.maedr.util.MiscUte.getDevFacs;
import static com.zotoh.maedr.util.MiscUte.getDevs;
import static com.zotoh.maedr.util.MiscUte.getUserDevCZ;
import static com.zotoh.maedr.util.MiscUte.loadConf;
import static com.zotoh.maedr.util.MiscUte.saveConf;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.json.JSONObject;

import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.MetaUte;
import com.zotoh.maedr.core.CmdHelpError;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.impl.DefaultDeviceFactory;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdDevice extends Cmdline {

	private DeviceManager<?,?> _dummyDevMgr;
	
	private void iniz() {
		try {
			_dummyDevMgr= (DeviceManager<?,?>) MetaUte.loadClass("com.zotoh.maedr.wflow.MockDeviceMgr")
			.getConstructor().newInstance();
		} 
		catch (Throwable t) {}
		if (_dummyDevMgr==null)
		try {
			_dummyDevMgr= (DeviceManager<?,?>) MetaUte.loadClass("com.zotoh.maedr.process.MockDeviceMgr")
			.getConstructor().newInstance();
		} 
		catch (Throwable t) {}
	}
	
    /**
     * @param home
     * @param cwd
     */
    public CmdDevice(File home, File cwd) {
        super(home, cwd);
        iniz();
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("device");
        }};
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    protected void eval(String[] args) throws Exception {
        if (args==null || args.length < 3) { 
            throw new CmdHelpError();
        }
        String s1= args[1], s2=args[2];        

        if ("?".equals(s2) && "configure".equals(s1)) {
        	listDevs();
        	return;
        }
        
        assertAppDir();
        
        if ("add".equals(s1)) {
        	addDev(s2);
        } 
        else if ("configure".equals(s1)) {
        	cfgDev(s2);
        } else {
        	throw new CmdHelpError();
        }
        
    }
    
    private void listDevs() throws Exception {    	
    	Set<String> set=DefaultDeviceFactory.getDevCZMap().keySet();
    	for (String s : set) {
    		System.out.format("%s\n", s);
    	}
    }
    
    private void cfgDev(String dev) throws Exception {
        JSONObject top= loadConf( getCwd());
        if  (! existsDevice(top, dev)) {
            throw new Exception("Unknown device type: " + dev);                        
        }
        Class<?> z;
        if ( DefaultDeviceFactory.getAllDefaultTypes().contains(dev)) {
            z=DefaultDeviceFactory.getDevCZ(dev);
        }  else {
            z=getUserDevCZ(top, dev);
        }
        
        if (z==null) {
            throw new ClassNotFoundException("Class not found for device: " + dev);
        }
        
        Device d= (Device) z.getConstructor(DeviceManager.class)
                .newInstance(_dummyDevMgr);
        Properties props= new Properties();
        boolean ok=false;
        
        if (d != null && d.supportsConfigMenu()) {
            ok=d.showConfigMenu(rcb(),props);
        }
        
        if (!ok) { return; }
        
        String id= nsb( props.remove("_id"));
        tstEStrArg("device id", id);
        props.put("type", dev);
        JSONObject obj= new JSONObject(props);
        JSONObject g= getDevs(top);
        
        
        if (g.has(id)) {
        	throw new Exception("Another device with name \"" + id + "\" is defined already");
        } else {
        	g.put(id, obj);
        }
        
        saveConf( getCwd(), top) ;        
    }
    
    
    private void addDev(String dev) throws Exception {
    	
        JSONObject top=loadConf( getCwd());
        if ( existsDevice(top, dev)) {
            throw new Exception("Device type: " + dev + " already exists");            
        }
        
        final CmdLineQuestion q1= new CmdLineQuestion("dev", getResourceStr(rcb(), "cmd.devimpl.class") ) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("device", answer);
                return "";
            }};
        final CmdLineQuestion q0= new CmdLineQuestion("type", getResourceStr(rcb(),"cmd.dev.type") ) {
                protected String onAnswerSetOutput(String answer, Properties props) {
                    props.put("type", answer);
                    return "dev";
                }};
        CmdLineSequence s= new CmdLineSequence(q0,q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
        Properties props= new Properties();
        props.put("type", dev);
        s.start(props);
        if (s.isCanceled()) { return; }
        
        String fac=props.getProperty("device");
        String type=props.getProperty("type");
        tstEStrArg("device-class", fac);
        tstEStrArg("type", type);
        
        JSONObject devhdlrs= getDevFacs(top);
        devhdlrs.put(type, fac);
        
        saveConf( getCwd(), top);
    }
    
    
    
    
    
}


