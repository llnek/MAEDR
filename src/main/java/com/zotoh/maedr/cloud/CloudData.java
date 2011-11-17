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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asFileUrl;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.JSONUte.asString;
import static com.zotoh.core.util.JSONUte.read;
import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.zotoh.cloudapi.core.CloudAPI;
import com.zotoh.cloudapi.core.Vars;
import com.zotoh.core.crypto.PwdFactory;

/**
 * @author kenl
 *
 */
public enum CloudData implements Vars {
    
    INSTANCE;
    
    private String _pathToFile="";
    private CloudAPI _api;
    private JSONObject _cfg;

    
    
    /**
     * @return
     */
    public static CloudData getInstance() { return INSTANCE; }
    
    /**
     * @param f
     * @return
     * @throws Exception
     */
    public CloudData setDataPath(File f) throws Exception {
        _pathToFile= niceFPath(f); 
        return this;
    }
    
    /**
     * @throws JSONException
     * @throws IOException
     */
    public void save() throws JSONException, IOException {
        if (isEmpty(_pathToFile) ) { return; }
        if (_cfg == null) { return; }        
        writeFile( new File(_pathToFile), asString(_cfg));
    }
    
    /**
     * @throws FileNotFoundException
     * @throws JSONException
     */
    public void load() throws FileNotFoundException, JSONException {
        InputStream inp= null;
        String v;
        try {           
            _cfg= read(inp= readStream(new File(_pathToFile))) ;            
            v=getCredential().optString(P_VENDOR);
            _api= CloudAPI.create(v);
        }
        finally {
            close(inp);
        }        
    }
    
    /**
     * @return
     */
    public CloudAPI getAPI() { return _api; }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getCredential() throws JSONException {
        JSONObject obj=  _cfg==null ? null : _cfg.optJSONObject(P_CRED) ;
        if (obj==null && _cfg != null) {
            _cfg.put(P_CRED, obj=new JSONObject());
        }
        return obj;
    }
    
    /**
     * @param user
     * @param pwd
     * @param keyfile
     * @throws Exception
     */
    public void setSSHInfo(String user, String pwd, String keyfile) throws Exception {
        JSONObject obj= getSSHInfo();
        obj.put(P_USER, nsb(user));
        
        if (!isEmpty(pwd)) {
            pwd=PwdFactory.getInstance().create(pwd).getAsEncoded() ;
        }
        obj.put(P_PWD, nsb(pwd));
        
        if (! isEmpty( keyfile)) {
            keyfile= asFileUrl( niceFPath( new File(keyfile)) );
        }
        
        obj.put(P_KEY, nsb(keyfile));
    }
    
    /**
     * @param vendor
     * @param acct
     * @param id
     * @param pwd
     * @throws JSONException
     */
    public void setConfig(String vendor, String acct, String id, String pwd) throws Exception {
        tstEStrArg("cloud-vendor", vendor);
        tstEStrArg("cloud-account", acct);
//        tstEStrArg("cloud-id", id);
//        tstEStrArg("cloud-pwd", pwd);        
        JSONObject obj= getCredential();        
        vendor=vendor.toLowerCase();
        
        if ("aws".equals(vendor)) { vendor="amazon"; }        
        if (!isEmpty(pwd)) {
        	// obfuscate the password
        	pwd= PwdFactory.getInstance().create(pwd).getAsEncoded();
        }
        
        obj.put(P_VENDOR, vendor);
        obj.put(P_ACCT, acct);
        obj.put(P_ID, nsb(id));
        obj.put(P_PWD, nsb(pwd));
        
        _api= CloudAPI.create(vendor);
    }
    
    /**
     * @return
     * @throws JSONException
     */
    public String getVendor() throws JSONException {
    	return nsb( getCredential().optString( P_VENDOR ));
    }
    
    /**
     * @return
     * @throws JSONException
     */
    public boolean isAWS() throws JSONException {
    	String v= getVendor();
    	return "aws".equalsIgnoreCase(v) || "amazon".equalsIgnoreCase(v) ;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getSSHKeys() throws JSONException {
        JSONObject obj=   _cfg==null ? null : _cfg.optJSONObject(P_KEYS) ; 
        if (obj==null && _cfg != null) {
            _cfg.put(P_KEYS, obj=new JSONObject());
        }
        return obj;
    }
    
    /**
     * @return
     * @throws JSONException
     */
    public JSONObject getRegions() throws JSONException {
        JSONObject obj=   _cfg==null ? null : _cfg.optJSONObject(P_REGIONS) ; 
        if (obj==null && _cfg != null) {
            _cfg.put(P_REGIONS, obj=new JSONObject());
        }
        return obj;    	
    }
    
    /**
     * @param rgs
     * @throws JSONException
     */
    public void setRegions(JSONObject rgs) throws JSONException {
    	if (_cfg != null) {
    		if (_cfg.has(P_REGIONS)) { _cfg.remove(P_REGIONS); }
    		_cfg.put(P_REGIONS, rgs);
    	}
    }
    
    /**
     * @return
     * @throws JSONException
     */
    public Set<String> listRegions() throws JSONException {
    	JSONObject r= getRegions();
    	Set<String> rc= ST();
    	for (Iterator<?> it = r.keys(); it.hasNext(); ) {
    		rc.add( nsb( it.next() ) ) ;
    	}
    	return Collections.unmodifiableSet(rc) ;
    }

    /**
     * @param region
     * @return
     * @throws JSONException
     */
    public Set<String> listZones(String region) throws JSONException {
    	JSONObject r= getRegions(),
    					zs=r.optJSONObject(region) ;
    	Set<String> rc= ST();
    	if (zs != null) {
        	for (Iterator<?> it = zs.keys(); it.hasNext(); ) {
        		rc.add( nsb(it.next()) );
        	}    		
    	}
    	return Collections.unmodifiableSet(rc) ;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getFirewalls() throws JSONException {
        JSONObject obj= _cfg==null ? null : _cfg.optJSONObject(P_FWALLS) ; 
        if (obj==null && _cfg != null) {
            _cfg.put(P_FWALLS, obj=new JSONObject());
        }
        return obj;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getIPs() throws JSONException {
        JSONObject obj=  _cfg==null ? null : _cfg.optJSONObject(P_IPS) ; 
        if (obj==null && _cfg != null) {
            _cfg.put(P_IPS, obj=new JSONObject());
        }
        return obj;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getServers() throws JSONException {
        JSONObject obj=  _cfg==null ? null : _cfg.optJSONObject(P_VMS) ; 
        if (obj==null && _cfg != null) {
            _cfg.put(P_VMS, obj=new JSONObject());
        }
        return obj;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getImages() throws JSONException {
        JSONObject obj=  _cfg==null ? null : _cfg.optJSONObject(P_IMAGES) ;
        if (obj==null && _cfg != null) {
            _cfg.put(P_IMAGES, obj=new JSONObject());
        }
        return obj;        
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getSSHInfo() throws JSONException {
        JSONObject obj=  _cfg==null ? null : _cfg.optJSONObject(P_SSHINFO) ;
        if (obj==null && _cfg != null) {
            _cfg.put(P_SSHINFO, obj=new JSONObject());
        }
        return obj;        
    }

    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultRegion() throws JSONException {
        return nsb ( getDefaults().optString(P_REGION) );
    }
    
    /**
     * @param region
     * @throws JSONException
     */
    public void setDefaultRegion(String region) throws JSONException {
        getDefaults().put(P_REGION, trim(region));
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultServer() throws JSONException {
        return nsb ( getDefaults().optString(P_VM) );
    }
    
    /**
     * @param vm
     * @throws JSONException
     */
    public void setDefaultServer(String vm) throws JSONException {
        getDefaults().put(P_VM, trim(vm));
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultImage() throws JSONException {
        return nsb ( getDefaults().optString(P_IMAGE) );
    }
    
    /**
     * @param image
     * @throws JSONException
     */
    public void setDefaultImage(String image) throws JSONException {
        getDefaults().put(P_IMAGE, trim(image));
    }
    
    
    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultKey() throws JSONException {
        return nsb ( getDefaults().optString(P_KEY) );
    }
    
    /**
     * @param key
     * @throws JSONException
     */
    public void setDefaultKey(String key) throws JSONException {
        getDefaults().put(P_KEY, trim(key));
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultFirewall() throws JSONException {
        return nsb ( getDefaults().optString(P_FWALL) );
    }
    
    /**
     * @param fw
     * @throws JSONException
     */
    public void setDefaultFirewall(String fw) throws JSONException {
        getDefaults().put(P_FWALL, trim(fw));
    }

    /**
     * @return
     * @throws JSONException
     */
    public String getDefaultZone() throws JSONException {
        return nsb ( getDefaults().optString(P_ZONE) );
    }
    
    /**
     * @param z
     * @throws JSONException
     */
    public void setDefaultZone(String z) throws JSONException {
        getDefaults().put(P_ZONE, trim(z));
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public String getDefaultProduct(String arch) throws JSONException {
        JSONObject obj= getDefaults().optJSONObject(P_PRODUCT);
        return arch==null || obj==null ? "" : nsb ( obj.optString(arch) );
    }
    
    /**
     * @param type
     * @throws JSONException
     */
    public void setDefaultProduct(String arch, String ptype) throws JSONException {
        tstEStrArg("architecture-key", arch);
        tstEStrArg("architecture", ptype);
        JSONObject def= getDefaults(),
                obj= def.optJSONObject(P_PRODUCT);
        if (obj==null) {
            def.put(P_PRODUCT,  obj= new JSONObject());
        }
        obj.put(arch, ptype);
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public JSONObject getCustomProps() throws JSONException {
        JSONObject obj= _cfg==null ? null : _cfg.optJSONObject(P_CUSTOM) ;
        if (obj==null && _cfg != null ) {
            _cfg.put(P_CUSTOM, obj=new JSONObject());
        }
        String v= nsb ( getCredential().optString(P_VENDOR) );
        return obj.optJSONObject( v );
    }
    
    private JSONObject getDefaults() throws JSONException {
        JSONObject obj= _cfg==null ? null : _cfg.optJSONObject(P_DFTS) ;
        if (obj==null && _cfg != null ) {
            _cfg.put(P_DFTS, obj=new JSONObject());
        }
        return obj;
    }
    
    
    private CloudData() {}
        
}
