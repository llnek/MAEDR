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
 
package com.zotoh.maedr.cloud;

import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.errBadArg;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;
import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.ProcessUte.safeThreadWait;
import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.join;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.nsn;
import static com.zotoh.core.util.StrUte.strstr;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.DataCenterServices;
import org.dasein.cloud.dc.Region;
import org.dasein.cloud.identity.ShellKeySupport;
import org.dasein.cloud.network.AddressType;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.IpAddress;
import org.dasein.cloud.network.IpAddressSupport;
import org.dasein.cloud.network.Protocol;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.cloudapi.aws.AWSInitContext;
import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.util.GUID;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 *  Deals with all the cloud related setup and invocations.
 *  (Internal use only)
 *  
 * @author kenl
 */
public enum Cloudr implements com.zotoh.maedr.core.Vars, 
com.zotoh.cloudapi.core.Vars {
    
    INSTANCE;
    
    private Logger ilog() {  return _log=getLogger(Cloudr.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private ResourceBundle _rcb;
    private CloudProvider _prov;
    private File _appDir, _cfgDir;
    
    /**
     * @param appDir
     * @param rc
     * @throws Exception
     */
    public void iniz(File appDir, ResourceBundle rc) throws Exception {
        
        tlog().debug("CloudRunner: iniz()");
        
        tstObjArg("app-dir", appDir);
        
        _cfgDir=new File(appDir, CFG);
        _rcb=rc;
        _appDir= appDir;
        
        CloudData data= CloudData.getInstance().setDataPath(
                new File( _cfgDir, CLOUDDATA));
        data.load();

        String v= data.getCredential().optString(P_VENDOR) ;
        if ("amazon".equalsIgnoreCase(v) || "aws".equalsIgnoreCase(v) ) {        	
            inizAWS(data);
        } else {
            throw new Exception("Unknown cloud provider: " + v) ;
        }
    }
      
    /**
     * @param vendor
     * @param acct
     * @param id
     * @param pwd
     * @throws Exception
     */
    public void setConfig(String vendor, String acct, String id, String pwd) throws Exception {
        CloudData.getInstance().setConfig(vendor, acct, id, pwd);
        CloudData.getInstance().save();
        doSave();
        iniz(_appDir,_rcb);            	
    }
    
    /**
     * @param user
     * @param pwd
     * @param keyfile
     * @throws Exception
     */
    public void setSSHInfo(String user, String pwd, String keyfile) throws Exception {
        CloudData.getInstance().setSSHInfo(user, pwd, keyfile);
        CloudData.getInstance().save();
        doSave();        
    }
    
    /**
     * 
     */
    public void finz() {
        tlog().debug("CloudRunner: finz()");        
        _prov=null; 
    }
    
    /**/
    private void inizAWS(CloudData data) throws Exception {
        JSONObject c= data.getCustomProps();
        Properties props= new Properties();
    	String p, rg=data.getDefaultRegion();
    	
        if (c != null) for (Iterator<?> it =c.keys(); it.hasNext(); ) {
            p= nsb( it.next());
            props.put(p, nsb( c.opt(p)) );
        }
        c= data.getCredential();
        props.put(P_ACCT, nsb( c.optString(P_ACCT)) );
        props.put(P_ID, nsb( c.optString(P_ID)) );
        props.put(P_PWD, nsb( c.optString(P_PWD)) );
        props.put(P_REGION, nsb ( rg) );
        
        _prov= AWSInitContext.getInstance().configure(props) ;        
    }
    
    /**
     * @return
     */
    public CloudProvider getProvider() { return _prov; }
        
    /**
     * @return
     */
    public static Cloudr getInstance() { return INSTANCE; }

    /**
     * @param arch
     * @return
     * @throws Exception
     */
    public String getProduct(String arch) throws Exception {
        return CloudData.getInstance().getDefaultProduct(arch) ;
    }
    
    /**
     * @param props
     * @param version
     * @param target
     * @throws Exception
     */
    public void install(Properties props, String version, String target) throws Exception {
    	
        tstEStrArg("target host@folder", target);
        tstEStrArg("version", version);
        tstObjArg("sshinfo", props);
        
        String script= rc2Str("com/zotoh/maedr/util/remote_install.txt", "utf-8") ;
        String[] ss= target.split(":");
        if (ss==null || ss.length != 2) {
            errBadArg("target host:folder > " + target);            
        }

        String keyfile= trim( props.getProperty("key"));
        String user= trim( props.getProperty("user"));
        String pwd= trim( props.getProperty("pwd"));
        
        String rfile=GUID.generate();
        String host= trim(ss[0]) ;
        String dir= trim(ss[1]) ;
        
        tlog().debug("Cloud.install() version = {}", version) ;
        tlog().debug("Cloud.install() host = {}", host) ;
        tlog().debug("Cloud.install() dir = {}", dir) ;
        
        tlog().debug("Cloud.install() user = {}", user) ;
        tlog().debug("Cloud.install() pwd = {}", isEmpty(pwd)?"null":"****") ;
        tlog().debug("Cloud.install() key = {}", keyfile) ;
        
        script=strstr(script, "${MAEDR_VERSION}", version);
        script=strstr(script, "${TARGET_DIR}", dir);
        
        if ( !isEmpty(keyfile)) {
            File f= new File(keyfile);
            if ( !f.exists() || !f.canRead()) {
                errBadArg("key file does not exist or is not readable");
            }
            keyfile=niceFPath(f) ;
        }
        
//        tlog().debug("{}" , script);
        
        SSHUte.scp( host, 22, user, pwd, keyfile, 
                asBytes(script), rfile, "/tmp", "0700");        
        
        if ( SSHUte.rexec( true, host, user, pwd, keyfile, rfile, "/tmp", "Installed OK.") ) {
            //System.out.format("MAEDR installed OK.\n");
            System.out.format("\n");
        } else {
            System.out.format("Failed to install MAEDR.\n");            
        }
                
    }
    
    /**
     * @param run
     * @param props
     * @param target
     * @throws Exception
     */
    public void deploy(boolean run, Properties props, String target) throws Exception {
    	
        tstEStrArg("target host@folder", target);
        tstObjArg("sshinfo", props);

        String script= rc2Str("com/zotoh/maedr/util/remote_deploy.txt", "utf-8") ;
        String[] ss= target.split(":");
        if (ss==null || ss.length != 2) {
            errBadArg("target host:folder > " + target);            
        }

        String keyfile= trim( props.getProperty("key"));
        String user= trim( props.getProperty("user"));
        String pwd= trim( props.getProperty("pwd"));
        String maedr= "";
        
        String localFile= trim( props.getProperty("bundle"));
        tstEStrArg("application bundle", localFile);           
        
        if (run) {
            maedr= trim( props.getProperty("maedr"));
            tstEStrArg("remote MAEDR home", maedr);        	
        }        
        
        String host= trim(ss[0]) ;
        String dir= trim(ss[1]) ;
        
        File fp= new File( localFile);        
        String rfile=GUID.generate() ,
        				action="",
        				fn= fp.getName() ,
        rdir=strstr(fn, ".tar.gz", "");
        rdir=strstr(rdir, ".zip", "");
        				
        
        tlog().debug("Cloud.install() host = {}", host) ;
        tlog().debug("Cloud.install() dir = {}", dir) ;
        
        tlog().debug("Cloud.install() user = {}", user) ;
        tlog().debug("Cloud.install() pwd = {}", isEmpty(pwd)?"null":"****") ;
        tlog().debug("Cloud.install() key = {}", keyfile) ;

        if (!isEmpty(keyfile)) {
            File f= new File(keyfile);
            if ( !f.exists() || !f.canRead()) {
                errBadArg("key file does not exist or is not readable");
            }
            keyfile=niceFPath(f) ;
        }
                        
        if (fn.endsWith(".tar.gz")) { action="tar xvfz "; }
        else
        if (fn.endsWith(".zip")) { action="unzip "; }
        else {
            errBadArg("bundle file must be tarzipped or ziped");
        }
        
        script=strstr(script, "${TARGET_FILE}",  "/tmp/" + fn);
        script=strstr(script, "${TARGET_DIR}", dir);
        script=strstr(script, "${TARGET_FILE_DIR}", rdir );
        script=strstr(script, "${MAEDR_HOME}", maedr );        
        script=strstr(script, "${UNPACK_ACTION}", action );
                
        SSHUte.scp(host, 22, user, pwd, keyfile, "/tmp", fp, "0644");        
        SSHUte.scp(host, 22, user, pwd, keyfile, 
                        asBytes(script), rfile, "/tmp", "0700");        
                
        if ( SSHUte.rexec( true, host, user, pwd, keyfile, rfile, "/tmp", "Deployed OK.") ) {
            System.out.format("\n");
        } else {
            System.out.format("Failed to deploy application.\n");            
        }
    }
    
    /**
     * @return
     */
    public String getImage() throws Exception {
        return  CloudData.getInstance().getDefaultImage() ;
    }
    
    /**
     * @throws Exception
     */
    public void syncRegions() throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
    	DataCenterServices svc=_prov.getDataCenterServices();
    	Collection<Region> lst= svc.listRegions(); 
    	Properties ps= new Properties();
    	CloudData data=CloudData.getInstance();
    	boolean ok=false;
    	String s, s0;
    	String def=data.getDefaultRegion();
    	JSONObject rgs=data.getRegions();
    	
        System.out.format("%s\n%s\n", 
                (s0=getResourceStr(_rcb, "cmd.available.regions")+":"),
                underline(s0.length()));
                
    	for (Region r: lst) {    		
    		s=r.getName();
    		ps.put(s, r.getProviderRegionId()) ;
    		if (!rgs.has(s)) {
    			rgs.put(s, new JSONObject());
    		}
    		if (s.equals(def)) { ok=true; }
    		System.out.format("%s\n", s);
    	}
    	
    	if (!ok) { data.setDefaultRegion(""); }    	
    	data.getAPI().setProperties( "endpoint", data.getCustomProps(), ps) ;
    	
    	preSave();
    	doSave();
    	
        data.getAPI().setRegionsAndZones( data.getRegions()) ;
    }
    
    /**
     * @throws Exception
     */
    public void syncDatacenters() throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
    	DataCenterServices svc=_prov.getDataCenterServices();
    	CloudData data=CloudData.getInstance();
    	JSONObject j, rgs=data.getRegions();    	
    	Collection<DataCenter> dc;
    	String s0, s, z;
    	
        System.out.format("%s\n%s\n", 
                (s0=getResourceStr(_rcb, "cmd.available.zones")+":"),
                underline(s0.length()));
    	
    	for (Iterator<?> it= rgs.keys(); it.hasNext(); ) {
    		s= nsb( it.next());
    		j= rgs.optJSONObject(s) ;
    		if (j==null) { continue; }	// error! file is corrupt ??? 
    		dc=svc.listDataCenters(s);
    		for (DataCenter c : dc) {
    			z=c.getProviderDataCenterId();
    			j.put(z, new JSONObject());
                System.out.format("%s) %s\n", s, z);
    		}
    	}
    	
        preSave();
    	doSave();
    	
        data.getAPI().setRegionsAndZones( data.getRegions()) ;    	
    }
        
    /**
     * @param image
     */
    public void setImage(String image) throws Exception {
        
        if (!isEmpty(image)) 
        try {
            
            System.out.format("%s\n",
                    getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
            System.out.format("%s\n",
                    getResourceStr(_rcb, "cmd.cloud.image.info"));
            
            MachineImage mi=_prov.getComputeServices().getImageSupport().getMachineImage(image);
            if (mi != null) {
                CloudData data=CloudData.getInstance();
                data.setDefaultImage(image) ;
                JSONObject obj= data.getImages(),
                j=new JSONObject();
                
                j.put(P_PLATFORM, mi.getPlatform().isLinux() ? PT_LINUX : PT_WINDOWS) ;
                j.put(P_ARCH, mi.getArchitecture().name()) ;
                obj.put(image, j);
                
                System.out.format("%s: %s\n%s: %s\n", 
                        getResourceStr(_rcb, "cmd.image.platform"), j.optString(P_PLATFORM),
                        getResourceStr(_rcb, "cmd.image.arch"),
                            I32.equals(mi.getArchitecture()) ? getResourceStr(_rcb, "cmd.32bit") : getResourceStr(_rcb, "cmd.64bit")
                        );
            }
        }
        catch (Exception e){
            error(e);
            tlog().warn("",e);
        }
        
        preSave();
        
        CloudData.getInstance().setDefaultImage( image ) ;
        
        doSave();        
    }

    /**
     * @return
     */
    public String getServer() throws Exception {
        return CloudData.getInstance().getDefaultServer() ;
    }
    
    /**
     * @param vm
     */
    public void setServer(String vm) throws Exception {
        CloudData.getInstance().setDefaultServer( vm) ;
        CloudData.getInstance().save();
    }
    
    /**
     * @return
     */
    public String getSSHKey() throws Exception {
        return CloudData.getInstance().getDefaultKey();
    }
    
    /**
     * @param key
     */
    public void setSSHKey(String key) throws Exception {
        CloudData.getInstance().setDefaultKey(key) ;
        CloudData.getInstance().save();
    }

    /**
     * @return
     * @throws Exception
     */
    public String getDatacenter() throws Exception {
        return CloudData.getInstance().getDefaultZone() ;
    }

    /**
     * @param dc
     * @throws Exception
     */
    public void setDatacenter(String dc) throws Exception {
        CloudData.getInstance().setDefaultZone( dc ) ;
        CloudData.getInstance().save();
    }
    
    /**
     * @return
     */
    public String getRegion() throws Exception {
        return CloudData.getInstance().getDefaultRegion() ;
    }
    
    /**
     * @param region
     */
    public void setRegion(String region) throws Exception {
        CloudData.getInstance().setDefaultRegion( region) ;
        CloudData.getInstance().save();
    }
    
    /**
     * @return
     */
    public String getSecgrp() throws Exception {
        return CloudData.getInstance().getDefaultFirewall() ;
    }
    
    /**
     * @param grp
     */
    public void setSecgrp(String grp) throws Exception {
        CloudData.getInstance().setDefaultFirewall( grp );
        CloudData.getInstance().save();
    }
    
    /**
     * @param image
     * @param ptype
     * @param key
     * @param groups
     * @param zone
     * @throws Exception
     */
    public void launchImage(String image, 
            String ptype, String key, 
            String[] groups, 
            String region, String zone) throws Exception {
        
        if (isEmpty(image)) {            image= getImage();        }
        
        tstEStrArg("product or instance type", ptype);
        tstEStrArg("image-id", image);
        tstEStrArg("ssh key name", key);
        tstEStrArg("region", region);
        tstObjArg("groups", groups);

        tlog().debug("LaunchImage: groups {} " ,  join(groups, "|"));
        tlog().debug("LaunchImage: region {} " , region);
        tlog().debug("LaunchImage: zone {} " , nsn( zone));
        
        tlog().debug("LaunchImage: product {} " ,  ptype);
        tlog().debug("LaunchImage: image {} " ,  image);
        tlog().debug("LaunchImage: key {} " , key);
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
        
        VirtualMachineSupport vs= _prov.getComputeServices().getVirtualMachineSupport();
        String name="", userdata="", vpc="", regionzone= region+"|" + nsb(zone);

        VirtualMachine vm= vs.launch(image, vs.getProduct(ptype), 
        				regionzone, name, userdata, key, 
        				vpc, false, false, groups);        
        
        if (vm != null) {
        	testVmReady(vm, vm.getProviderVirtualMachineId()) ;
        }
        
    }
    
    private void testVmReady(VirtualMachine vm, String vmid) throws Exception {
    	
    	VirtualMachineSupport vs= _prov.getComputeServices().getVirtualMachineSupport();
    	VmState st;
    	
    	String name="", ip="", s1=getResourceStr(_rcb,"cmd.vm.lbl")+": ",
    					s2=getResourceStr(_rcb,"cmd.state")+": ",
						s3=getResourceStr(_rcb, "cmd.check.wait", "8");
    	
    	if (vm==null) {
    		vm= vs.getVirtualMachine(vmid) ; 
    	}
    	
        while (vm != null) {
        	
        	name= vm.getProviderVirtualMachineId();
        	st=vm.getCurrentState();
        	
        	System.out.format("%-32s%-30s\n", s1 + name,	s2 + st.name() ); 
        	
        	if ( ! VmState.PENDING.equals(st)) { 
    		break; }

        	System.out.format("%s\n",  s3);
        	
        	safeThreadWait(8000) ;        	
        	vm=vs.getVirtualMachine(name) ;
        }
        
        if (vm != null) {
        	ip=vm.getPublicDnsAddress();
            st=vm.getCurrentState();
        	System.out.format("%-32s%-30s%-24s\n", s1+name, s2+st.name(),  
        					getResourceStr(_rcb,"cmd.public.dns") +": " + ip);
        }
        
        if (!isEmpty(ip)) {
        	CloudData data= CloudData.getInstance();
        	JSONObject obj=data.getServers(), j= new JSONObject();
        	j.put(P_PUBDNS, ip);
        	obj.put(name, j);
        	data.save();
        }
        
    }
    
    /**
     * @param vmid
     * @throws Exception
     */
    public void startServer(String vmid) throws Exception {
    	
    	if (isEmpty(vmid)) { vmid= getServer(); }
    	
    	tstEStrArg("server-hostvm-id", vmid);
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
    	
        VirtualMachineSupport vs= _prov.getComputeServices().getVirtualMachineSupport();
    	vs.boot(vmid);
    	
    	testVmReady(null, vmid) ;    	
    }
    
    /**
     * @param vmid
     * @throws Exception
     */
    public void terminateServer(String vmid) throws Exception {
    	
    	if (isEmpty(vmid)) { vmid= getServer(); }
    	
    	tstEStrArg("server-hostvm-id", vmid);
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            

        VirtualMachineSupport vs= _prov.getComputeServices().getVirtualMachineSupport();
        vs.terminate(vmid);

        CloudData data= CloudData.getInstance();
        data.getServers().remove(vmid);
        
        preSave();
        doSave();        
    }
    
    /**
     * @param vmid
     * @throws Exception
     */
    public void stopServer(String vmid) throws Exception {
        
    	if (isEmpty(vmid)) { vmid= getServer(); }
        
    	tstEStrArg("vm-server-id", vmid);
        
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            

        VirtualMachineSupport vs= _prov.getComputeServices().getVirtualMachineSupport();
        VirtualMachine vm= vs.getVirtualMachine(vmid) ;        
        if (vm==null) { return; }
        if ( !vm.isPausable()) {
        	System.out.format("%s\n", getResourceStr(_rcb, "cmd.vm.not.pausable"));
        	return;
        }
        
        vs.pause(vmid);
        
        preSave();
        doSave();
    }
    
    /**
     * @param vmid
     * @throws Exception
     */
    public void descServer(String vmid) throws Exception {
        
    	if (isEmpty(vmid)) { vmid= getServer(); }
        
    	tstEStrArg("vm-server-id", vmid);
        
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            

        VirtualMachine vm= _prov.getComputeServices().getVirtualMachineSupport().getVirtualMachine(vmid) ;
        if (vm==null) { return; }
        CloudData data=CloudData.getInstance();
        JSONObject j, obj= data.getServers();
        
    	String st, dns;
    	
		vmid=vm.getProviderVirtualMachineId();
		st=vm.getCurrentState().name();
		dns=nsb( vm.getPublicDnsAddress());
		
        drawTable("\n%-16s%-8s%-12s%-16s%-20s\n", 
                getResourceStr(_rcb, "cmd.vmid"),
                getResourceStr(_rcb, "cmd.state"),
                getResourceStr(_rcb, "cmd.region"),
                getResourceStr(_rcb, "cmd.zone"),
                getResourceStr(_rcb, "cmd.public.ip"));                 
        System.out.format("%s\n", underline(78));
		
        System.out.format("%-16s%-8s%-12s%-16s%-20s\n", vmid,   st.charAt(0),   
                vm.getProviderRegionId(), vm.getProviderDataCenterId(),
                join(vm.getPublicIpAddresses(),"|"));
        System.out.format("%s-> %s\n", getResourceStr(_rcb, "cmd.public.dns"), dns);
        
    	obj.remove(vmid);    	
    	if (! "terminated".equalsIgnoreCase(st)) {
        	obj.put(vmid,  j=new JSONObject());
        	j.put(P_PUBDNS, dns);    		
            j.put(P_REGION, nsb(vm.getProviderRegionId()) );           
            j.put(P_ZONE, nsb(vm.getProviderDataCenterId()));           
    	}    	
    	
    	preSave();
    	doSave();
    }
    
    /**
     * @throws Exception
     */
    public void listServers() throws Exception {
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            

        CloudData data= CloudData.getInstance();
        JSONObject j, obj= data.getServers();
    	Iterable<VirtualMachine> lst= _prov.getComputeServices()
    					.getVirtualMachineSupport().listVirtualMachines();
    	VirtualMachine vm;
    	String s0, st, vmid, dns;
    	boolean ok= lst.iterator().hasNext();
    	
    	System.out.format("%s\n%s\n%s\n",
    	    	        (s0=getResourceStr(_rcb, "cmd.available.vms")+":"),
    	    	        underline(s0.length()),
    	    	        ok ? "" :  "none");
    	
    	if (ok) {    	    
    	    drawTable("%-16s%-8s%-12s%-16s%-20s\n", 
    	            getResourceStr(_rcb, "cmd.vmid"),
                    getResourceStr(_rcb, "cmd.state"),
                    getResourceStr(_rcb, "cmd.region"),
                    getResourceStr(_rcb, "cmd.zone"),
                    getResourceStr(_rcb, "cmd.public.ip"));    	            
    	    System.out.format("%s\n", underline(78));
    	}
    	
    	Set<String> bin= ST();
    	int pos=0;
    	for (Iterator<VirtualMachine> it= lst.iterator(); it.hasNext();) {
    		vm=it.next();
    		vmid=vm.getProviderVirtualMachineId();
    		st=vm.getCurrentState().name();
    		dns=nsb( vm.getPublicDnsAddress());
    		
    		if (pos > 0) { System.out.format("\n"); }
    		
        	System.out.format("%-16s%-8s%-12s%-16s%-20s\n", vmid,	st.charAt(0), 	
        	        vm.getProviderRegionId(), vm.getProviderDataCenterId(),
        	        join(vm.getPublicIpAddresses(),"|"));
        	System.out.format("%s-> %s\n", getResourceStr(_rcb, "cmd.public.dns"), dns);
        	
        	if ("terminated".equalsIgnoreCase(st)) {
        		obj.remove(vmid) ;
    		} else {
    			obj.remove(vmid);
    			obj.put(vmid, j=new JSONObject()) ;
                j.put(P_REGION, vm.getProviderRegionId()) ;
                j.put(P_ZONE, vm.getProviderDataCenterId()) ;
    			j.put(P_PUBDNS, dns) ;
        		bin.add(vmid); // keep track of the good ones
    		}
        	
        	++pos;
    	}
    	
    	// get rid of ones that don't exist anymore
    	JSONArray arr= obj.names();
    	int len= arr==null ? 0 : arr.length();
    	for (int i=0; i < len; ++i) {
    		vmid= nsb( arr.get(i));
    		if (! bin.contains(vmid)) {
    			obj.remove(vmid) ;
    		}
    	}
    	
    	preSave();
    	doSave();
    }
    
    /**
     * @param key
     * @throws Exception
     */
    public void removeSSHKey(String key) throws Exception {
    	
        if (isEmpty(key)) { return ; }
        
        ShellKeySupport ssh=_prov.getIdentityServices().getShellKeySupport();
        JSONObject obj;
        CloudData data=CloudData.getInstance();
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            

        ssh.deleteKeypair(key);
        if (key.equals( data.getDefaultKey())) {
            data.setDefaultKey("");
        }
        
        obj= data.getSSHKeys();
        if (obj.has(key)) {
            obj.remove(key) ;
        }
        
        System.out.format("%s\n", getResourceStr(_rcb, "cmd.deleted.key"));
        preSave();
        doSave();
    }
    
    /**
     * @throws Exception
     */
    public void listSSHKeys() throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
        
    	Collection<String> keys= _prov.getIdentityServices().getShellKeySupport().list();
    	CloudData data=CloudData.getInstance();
    	JSONObject obj= data.getSSHKeys();
    	String s0, def=data.getDefaultKey();
    	boolean ok=false;
    	
    	for (String s : keys) {
    		// check is current default is ok
    		if (s.equals(def)) { ok=true; }
    		if ( obj.has(s))  { continue; }
    		obj.put(s, new JSONObject());
    	}
    	
    	if (!ok) {
    		data.setDefaultKey("");
    	}
    	
    	System.out.format("%s\n%s\n%s\n",
    	        (s0=getResourceStr(_rcb, "cmd.available.keys")+":"),
    	        underline(s0.length()),
    	        keys.isEmpty() ? "none" :   join(keys, "\n"));
    	
    	preSave();
        doSave();
        
    }
    
    /**
     * @param key
     * @param path
     * @throws Exception
     */
    public void addSSHKey(String key, String path) throws Exception {
        
        ShellKeySupport ssh=_prov.getIdentityServices().getShellKeySupport();
        File out=new File(path);
        out.getParentFile().mkdirs();
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
        
        String pem= ssh.createKeypair(key);
        writeFile( out, pem, "utf-8");
        
        System.out.format("%s\n", getResourceStr(_rcb, "cmd.keyfile.saved", niceFPath(out))) ;
        
        CloudData data = CloudData.getInstance();
        JSONObject j, obj= data.getSSHKeys();
        boolean empty= !obj.keys().hasNext();
        
        obj.put(key, j=new JSONObject());
        pem= PwdFactory.getInstance().create(pem).getAsEncoded() ;
        j.put(P_PEM, pem);
        
        if (empty) {
        	data.setDefaultKey(key) ;
        }
        
        preSave();        
        doSave();
    }
    
    /**
     * @throws Exception
     */
    public void listFwalls() throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
        
        
        Collection<Firewall> walls= _prov.getNetworkServices().getFirewallSupport().list();
        CloudData data=CloudData.getInstance();
        JSONObject obj= data.getFirewalls();
        String s0, gn, def=data.getDefaultFirewall();
        boolean ok=false;
        StringBuilder b= new StringBuilder(1024);
        
        for (Firewall w : walls) {
            // check is current default is ok
            gn=w.getName();
            addAndDelim(b, "\n", gn);
            if (gn.equals(def)) { ok=true; }
            if ( obj.has(gn))  { continue; }
            obj.put(gn, new JSONObject());
        }
        if (!ok) {
            data.setDefaultFirewall("");
        }

        
        System.out.format("%s\n%s\n%s\n",
                (s0=getResourceStr(_rcb, "cmd.available.groups")+":"),
                underline(s0.length()),
                b.length()==0 ? "none" :   b.toString());
        
        preSave();
        doSave();
    }
    
    /**
     * @param fw
     * @throws Exception
     */
    public void removeFwall(String fw) throws Exception {
        
    	if (isEmpty(fw)) { return; }
        
    	FirewallSupport fs= _prov.getNetworkServices().getFirewallSupport();
        CloudData data= CloudData.getInstance();
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
                
        fs.delete(fw);
        
        if (fw.equals( data.getDefaultFirewall() )) {
            data.setDefaultFirewall("");
        }
        
        JSONObject obj= data.getFirewalls();
        if (obj.has(fw)) {
            obj.remove(fw);
        }
       
        System.out.format("%s\n", getResourceStr(_rcb, "cmd.deleted.group"));
        
        preSave();
        doSave();
    }
    
    /**
     * @param fw
     * @param desc
     * @throws Exception
     */
    public void addFwall(String fw, String desc) throws Exception {
        
    	tstEStrArg("firewall-securitygroup", fw);

        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
                        
        FirewallSupport fs= _prov.getNetworkServices().getFirewallSupport();
        fs.create(fw, desc);
        
        preSave();
        doSave();
    }
    
    /**
     * @param rule
     * @throws Exception
     */
    public void addCidr(String rule) throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
        Tuple t= splitRule(rule);
        
        FirewallSupport fs= _prov.getNetworkServices().getFirewallSupport();
        fs.authorize( nsb(t.get(0)), nsb(t.get(2)), (Protocol) t.get(1), (Integer) t.get(3), (Integer) t.get(4));
        
        preSave();
        doSave();        
    }
    
    /**
     * @param rule
     * @throws Exception
     */
    public void revokeCidr(String rule) throws Exception {
        
        System.out.format("%s\n",
                getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
        Tuple t= splitRule(rule);
        
        FirewallSupport fs= _prov.getNetworkServices().getFirewallSupport();
        fs.revoke( nsb(t.get(0)), nsb(t.get(2)), (Protocol) t.get(1), (Integer) t.get(3), (Integer) t.get(4));
        
        preSave();
        doSave();        
    }
    
    /**
     * @throws Exception
     */
    public void listEIPs() throws Exception {       
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));            
                    	
        Iterable<IpAddress>  itr= _prov.getNetworkServices().getIpAddressSupport().listPublicIpPool(false) ;
        String s0;
        boolean ok=itr.iterator().hasNext();
                
    	System.out.format("%s\n%s\n%s\n",
    	    	        (s0=getResourceStr(_rcb, "cmd.available.ips")+":"),
    	    	        underline(s0.length()),
    	    	        ok ? "" :  "none");
    	
    	if (ok) {    	    
    	    drawTable("%-20s%-16s%-16s\n", 
    	                    getResourceStr(_rcb, "cmd.public.ip"),
    	                    getResourceStr(_rcb, "cmd.region"),
    	            getResourceStr(_rcb, "cmd.vmid"));                    
    	    System.out.format("%s\n", underline(78));
    	}
    	
    	CloudData data=CloudData.getInstance();
    	Set<String> bin= ST();
    	String addr, rg, vmid;
    	JSONObject j, obj=data.getIPs();
    	
        for (IpAddress ip : itr) {
        	vmid=nsb(ip.getServerId());
        	addr=ip.getAddress();
        	rg=ip.getRegionId();
        	System.out.format("%-20s%-16s%-16s\n",	addr, rg, vmid );
        	bin.add(addr);
        	obj.remove(addr) ;
        	obj.put(addr,j=new JSONObject());
        	j.put(P_REGION, rg) ;
        	j.put(P_VM, vmid) ;
        }
         
    	// get rid of ones that don't exist anymore
    	JSONArray arr= obj.names();
    	int len= arr==null ? 0 : arr.length();
    	for (int i=0; i < len; ++i) {
    		addr= nsb( arr.get(i));
    		if (! bin.contains(addr)) {
    			obj.remove(addr) ;
    		}
    	}
        
    	preSave();
        doSave();    	                
    }
    
    public void removeEIP(String ip) throws Exception {       
        
    	System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
    	_prov.getNetworkServices().getIpAddressSupport().releaseFromPool(ip);
    	
    	JSONObject obj=CloudData.getInstance().getIPs();
    	obj.remove(ip);
    	
    	preSave();
    	doSave();
    }
    
    /**
     * @param region
     * @throws Exception
     */
    public void addEIP(String region) throws Exception {
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
        if (!isEmpty(region)) {
        	_prov.getContext().setRegionId(region) ;
        }

    	String addr= _prov.getNetworkServices().getIpAddressSupport().request(AddressType.PUBLIC) ;
    	
    	System.out.format("%s\n", addr) ;
    	
    	JSONObject j, obj=CloudData.getInstance().getIPs();
    	obj.put(addr, j=new JSONObject());
    	j.put(P_REGION, _prov.getContext().getRegionId()) ;
    	j.put(P_VM, "") ;
    	
    	preSave();
    	doSave();
    }
    
    /**
     * @param ip
     * @param vmid
     * @throws Exception
     */
    public void setEIP(String ip, String vmid) throws Exception {
    	
        System.out.format("%s\n",
                        getResourceStr(_rcb, "cmd.cloud.req.preamble"));
        
        IpAddressSupport svc=_prov.getNetworkServices().getIpAddressSupport();
        
        if ("0".equals(vmid)) {
        	svc.releaseFromServer(ip) ;
        } else {
        	svc.assign(ip, vmid) ;
        }
        
        safeThreadWait(3000);
        
        listEIPs();
    }
    
    /**/
    private Tuple splitRule(String rule) throws Exception {
        tstEStrArg("firewall-rule", rule);
        String g;
        int pos= rule.indexOf('@');
        if (pos < 0) { errBadArg("Malformed rule: " + rule); }
        g= rule.substring(0, pos);
        if (isEmpty(g)) { g= getSecgrp(); }
        tstEStrArg("firewall-securitygroup", g);
        rule= rule.substring(pos+1);
        String[] ss= rule.split("#");
        if (isNilArray(ss) || ss.length < 3) {errBadArg("Malformed rule: " + rule); }
        Protocol pc= "tcp".equals(ss[0]) ? Protocol.TCP : ("udp".equals(ss[0]) ? Protocol.UDP : null);
        tstObjArg("firewall-protocol", pc);
        String cidr=ss[1];
        int p1= asInt(ss[2], 0);
        int p2= ss.length > 3  ? asInt(ss[3], 0) : p1;
        tstPosIntArg("from-port", p1);
        tstPosIntArg("to-port", p2);
        return new Tuple(g, pc, cidr, p1, p2);
    }

    private void preSave() {
    	System.out.format("%s\n",
    	        "");
    	        //getResourceStr(_rcb, "cmd.cloud.cont"));
    }
    
    private void doSave() throws Exception {
    	CloudData.getInstance().save();
    	//System.out.format("%s\n", getResourceStr(_rcb, "cmd.cloud.success"));
    }
    
    private void error(Exception e) {
    	System.out.format("\n%s\n\n",
    	        getResourceStr(_rcb, "cmd.cloud.error",   e.getMessage()));
    }
    
    private String underline(int len) {
        StringBuilder b= new StringBuilder(512);
        for (int i=0; i < len; ++i) {
            b.append("-");
        }
        return b.toString();
    }
    
    private void drawTable(String fmt, Object...strs ) {
        System.out.format(fmt, strs);
    }
    
}


