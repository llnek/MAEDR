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
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.join;
import static com.zotoh.core.util.StrUte.nsb;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.json.JSONObject;

import com.zotoh.cloudapi.core.CloudAPI;
import com.zotoh.cloudapi.core.Vars;
import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.Tuple;
import com.zotoh.maedr.cloud.CloudData;
import com.zotoh.maedr.cloud.Cloudr;
import com.zotoh.maedr.core.CmdHelpError;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdCloud extends Cmdline implements Vars {

    /**
     * @param home
     * @param cwd
     */
    public CmdCloud(File home, File cwd) {
        super(home, cwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("cloud");
        }};
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    protected void eval(String[] args ) 
    				throws Exception {
        if (args==null || args.length < 2
                || !"cloud".equals(args[0]) ) {
            throw new CmdHelpError();
        }
        
        assertAppDir();
        
        String a4= args.length > 3 ? args[3] : "",
        a3= args.length > 2 ? args[2] : "",                
		a2= args[1];
                
        Cloudr cr= Cloudr.getInstance();        
        cr.iniz(getCwd(), _rcb);        
        
        System.out.println("");
        
        if ("configure".equals(a2)) {
            config(cr);
        }
        else if ("sshinfo".equals(a2)) {
            sshinfo(cr);
        }
        else if ("install".equals(a2) && !isEmpty(a3) && !isEmpty(a4)) {
        	Properties props= sshinfo();
        	if ( props != null) { cr.install(props, a3,a4); }
        }
        else if ("app/pack".equals(a2)) {
            tlog().debug("App-Pack called");
            runTarget("packzip-app");
        }
        else if (  ( "app/deploy".equals(a2) || "app/run".equals(a2) ) 
        				&& !isEmpty(a3)) {
        	boolean b= "app/run".equals(a2);
        	Properties props= preRemote(b);
        	if ( props != null) {
        		cr.deploy(  b, props, a3); 
    		}
        }
        else if ("sync".equals(a2) && !isEmpty(a3)) { 
           sync(cr, a3);
        }
        else if ("image/set".equals(a2) && !isEmpty(a3)) {
            cr.setImage(a3);            
        }
        else if ("image/*".equals(a2)) { 
            launchImage(cr);
        }
        else if ("vm/set".equals(a2) && !isEmpty(a3)) {
            cr.setServer(a3);
        }
        else if ("vm/?".equals(a2)) {
            cr.descServer(a3);
        }
        else if ("vm/*".equals(a2)) {
            cr.startServer(a3);
        }
        else if ("vm/!".equals(a2)) {
            cr.stopServer(a3);            
        }
        else if ("vm/%".equals(a2)) {
            cr.terminateServer(a3);            
        }
        else if ("sshkey/set".equals(a2) && !isEmpty(a3)) {
            cr.setSSHKey(a3);
        }
        else if ("sshkey/-".equals(a2) && !isEmpty(a3)) {
            cr.removeSSHKey(a3);
        }
        else if ("sshkey/+".equals(a2) && !isEmpty(a3)) {
            CmdLineSequence s= keyInput(a3);
            Properties props=new Properties();
            s.start(props);
            if (!s.isCanceled()) {
                cr.addSSHKey(a3, props.getProperty("fpath"));   
            }
        }
        else if ("ip/bind".equals(a2) && !isEmpty(a3) && !isEmpty(a4)) {
            cr.setEIP(a3, a4);
        }
        else if ("ip/-".equals(a2) && !isEmpty(a3)) {
            cr.removeEIP(a3);
        }
        else if ("ip/+".equals(a2) ) {
            CmdLineSequence s= addIpInput();
            Properties props=new Properties();
            s.start(props);
            if (!s.isCanceled()) {
                cr.addEIP(  props.getProperty("region"));            
            }
        }
        else if ("ip/list".equals(a2)) {
            cr.listEIPs();            
        }
        else if ("vm/list".equals(a2)) {
            cr.listServers();            
        }
        else if ("sshkey/list".equals(a2)) {
            cr.listSSHKeys();            
        }
        else if ("secgrp/list".equals(a2)) {
            cr.listFwalls();            
        }
        else if ("secgrp/set".equals(a2) && !isEmpty(a3)) {
            cr.setSecgrp(a3);
        }        
        else if ("secgrp/-".equals(a2) && !isEmpty(a3)) {
            cr.removeFwall(a3);
        }
        else if ("secgrp/+".equals(a2) && !isEmpty(a3)) {
            CmdLineSequence s= grpInput(a3);
            Properties props=new Properties();
            s.start(props);
            if (!s.isCanceled()) {
                cr.addFwall(a3, props.getProperty("desc"));            
            }
        }
        else if ("fwall/-".equals(a2)) {
            cr.revokeCidr(a3);
        }
        else if ("fwall/+".equals(a2)) {
            cr.addCidr(a3);            
        }
        else {
            throw new CmdHelpError();
        }
        
    }

    private Properties preRemote(final boolean b) throws Exception {
    	Properties props= new Properties();
    	CmdLineSequence s99= remoteInput();
    	
        CmdLineQuestion q1= new CmdLineMandatory("home",   getResourceStr(rcb(),"cmd.remote.maedr")) { 
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("maedr", answer);
                return "";
        }};    	
        final CmdLineQuestion q0= new CmdLineMandatory("bundle",   getResourceStr(rcb(),"cmd.maedr.bundlefile")) { 
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("bundle", answer);
                return b ? "home" : "";
        }};    	
        CmdLineSequence s= new CmdLineSequence(s99, q0,q1){
            protected String onStart() {
                return q0.getId();
            }            
        };
        s.start(props);
        
    	if (s.isCanceled()) { return null; }
    	else
    	{ return props; }
    }
    
    private void sync(Cloudr r, String task) throws Exception {
    	if ("datacenters".equals(task)) {
    		r.syncDatacenters();
    	}
    	else if ("regions".equals(task)) {
    		CloudData data=CloudData.getInstance();
    		r.syncRegions();    		
    		System.out.println("");
            CmdLineSequence  s= defRegInput();
            Properties props=new Properties();
            s.start(props);
            if (!s.isCanceled()) { 
	            String rg=props.getProperty("region");
	            data.setDefaultRegion(rg) ;
	            data.save();
            }
    	}
    	else {
    		throw new CmdHelpError();
    	}
    }
    
    private void config(Cloudr r) throws Exception {
        Properties props=new Properties();
        CmdLineSequence  s= cfgInput();
        s.start(props);
        if (s.isCanceled()) { return; }
        String vendor= props.getProperty("vendor");
        String acct= props.getProperty("acct");
        String id= props.getProperty("id");
        String pwd= props.getProperty("pwd");
        r.setConfig(vendor, acct, id, pwd) ;
    }
    
    private void sshinfo(Cloudr r) throws Exception {
        Properties props=sshinfo();
        if (props != null) {
        String user= props.getProperty("user");
        String pwd= props.getProperty("pwd");
        String key= props.getProperty("key");
        r.setSSHInfo(user, pwd, key) ;
        }
    }
    
    private Properties sshinfo() throws Exception {
        Properties props=new Properties();
        CmdLineSequence  s= remoteInput();
        s.start(props);
        if (s.isCanceled()) { return null; }
        else 
        { return props; }
    }
    
    private void launchImage(Cloudr r) throws Exception {
    	CloudData data= CloudData.getInstance();
    	CloudAPI api= data.getAPI();

        Tuple ts=new Tuple( api.listProducts(32) ,
        api.listProducts(64) ,
        api.listProducts(0) );
        
    	CmdLineSequence  s= launchInput(ts);
    	Properties props=new Properties();
    	s.start(props);
    	if (s.isCanceled()) { return; }
    	
        String[] groups= nsb(props.get("group")).split("(,|;)");
    	String image= props.getProperty("image");
        String ptype= props.getProperty("product");
        String key= props.getProperty("key");
        String region= props.getProperty("region");
        String zone= props.getProperty("zone");
        
        if (!isEmpty(zone)) {
            data.setDefaultZone(zone);
            data.save();
        }
        
    	r.launchImage(image, ptype, key, groups, region, zone);
    }

    private CmdLineSequence  remoteInput() throws Exception {
    	CloudData data=CloudData.getInstance();
        CmdLineQuestion q3= new CmdLineMandatory("key",   getResourceStr(rcb(),"cmd.ssh.keyfile")) { 
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("key", answer);
                return "";
            }};
        CmdLineQuestion q2= new CmdLineMandatory("pwd",   getResourceStr(rcb(),"cmd.user.pwd")) { 
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("pwd", answer);
                return "key";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("user",   getResourceStr(rcb(),"cmd.user.id"), "", 
        				nsb(data.getSSHInfo().optString(P_USER)) ) { 
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("user", answer);
                return "pwd";
            }};
        return new CmdLineSequence(q1,q2,q3){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    private CmdLineSequence  addIpInput() throws Exception {
    	CloudData data=CloudData.getInstance();
    	String rgs= join( data.getAPI().listRegions(), "\n");
        final CmdLineQuestion q1= new CmdLineMandatory("region",   getResourceStr(rcb(),"cmd.region"), 
        				rgs, data.getDefaultRegion()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("region", answer);
                return "";
            }};
        return new CmdLineSequence(q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    private CmdLineSequence  defRegInput() throws Exception {
    	String rgs= join( CloudData.getInstance().getAPI().listRegions(), "\n");
        final CmdLineQuestion q1= new CmdLineMandatory("region", 
                getResourceStr(rcb(),"cmd.setdef.region"),   rgs, "") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("region", answer);
                return "";
            }};
        return new CmdLineSequence(q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    private CmdLineSequence  keyInput(String keyname) throws Exception {
        final CmdLineQuestion q1= new CmdLineMandatory("pem", 
                getResourceStr(rcb(),"cmd.save.file"),   "", "cfg/"+keyname+".pem") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("fpath", answer);
                return "";
            }};
        return new CmdLineSequence(q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    private CmdLineSequence  grpInput(String grp) throws Exception {
        final CmdLineQuestion q1= new CmdLineMandatory("desc",   getResourceStr(rcb(),"cmd.brief.desc")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("desc", answer);
                return "";
            }};
        return new CmdLineSequence(q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
    private CmdLineSequence  cfgInput() throws Exception {
        CmdLineQuestion q4= new CmdLineMandatory("pwd", getResourceStr(rcb(),"cmd.cloud.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("pwd", answer);
                return "";
            }};
        CmdLineQuestion q3= new CmdLineMandatory("id", getResourceStr(rcb(),"cmd.cloud.id")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("id", answer);
                return "pwd";
            }};
        CmdLineQuestion q2= new CmdLineMandatory("acct", getResourceStr(rcb(),"cmd.cloud.acct")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("acct", answer);
                return "id";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("vendor", getResourceStr(rcb(),"cmd.cloud.vendor"),   "amazon", "amazon") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("vendor", answer);
                return "acct";
            }};
        return new CmdLineSequence(q1,q2,q3,q4){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }

    
    private CmdLineSequence  launchInput(final Tuple types) throws Exception {
    	
    	final CloudData data=CloudData.getInstance();
    	final CloudAPI api= data.getAPI();
    	
        api.setRegionsAndZones(data.getRegions());
    	Set<String> rgs= api.listRegions();
    	
        CmdLineQuestion q6= new CmdLineQuestion("group", getResourceStr(rcb(),"cmd.cloud.group"), 
                join(data.getFirewalls().keys(), "\n"), data.getDefaultFirewall()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("group", answer);
                return "";
            }};
        final CmdLineQuestion q5= new CmdLineQuestion("zone", getResourceStr(rcb(),"cmd.cloud.zone"), "", data.getDefaultZone()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("zone", answer);
                return "group";
            }};
        CmdLineQuestion q4= new CmdLineMandatory("region", getResourceStr(rcb(),"cmd.cloud.region"), join(rgs, "\n"), data.getDefaultRegion()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("region", answer);
                popZones(q5, api, answer);
                return "zone";
            }};
        CmdLineQuestion q3= new CmdLineMandatory("key", getResourceStr(rcb(),"cmd.cloud.key"), 
                join(data.getSSHKeys().keys(), "\n"), data.getDefaultKey()) {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("key", answer);
                return "region";
            }};
        final CmdLineQuestion q2= new CmdLineMandatory("ptype", getResourceStr(rcb(),"cmd.cloud.ptype"), "", "") {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("product", answer);
                return "key";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("img", getResourceStr(rcb(),"cmd.cloud.image"), "", data.getDefaultImage()) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("image", answer);
                imageBits(q2, answer);
                return "ptype";
            }};
        return new CmdLineSequence(q1,q2,q3,q4,q5,q6){
            protected String onStart() {
                return q1.getId();
            }           
        };
        
    }

    private void popZones(CmdLineQuestion q, CloudAPI api, String  region) {
        Set<String> rc= api.listDatacenters(region) ;
        q.setChoices( join(rc, "\n"));
    }
    
    private void imageBits(CmdLineQuestion q, String  image) {
    	List<String> ps;
    	int arch=0;
    	try {
			JSONObject obj= CloudData.getInstance().getImages();
			obj=obj.optJSONObject(nsb( image));
			String s= obj==null ? "" : obj.optString(P_ARCH);
			if (I64.equals(s)) arch=64;
			if (I32.equals(s)) arch= 32;
			s=CloudData.getInstance().getDefaultProduct(s);
			q.setDefaultAnswer(nsb(s));
		} 
    	catch (Exception e) {}		
		
    	ps= CloudData.getInstance().getAPI().listProductIds(arch);
    	q.setChoices( join(ps, "\n") );
    }
    
    
}


