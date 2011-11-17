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
 
package com.zotoh.maedr.util;

import static com.zotoh.core.io.StreamUte.readBytes;
import static com.zotoh.core.io.StreamUte.readFile;
import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.asString;
import static com.zotoh.core.util.MetaUte.loadClass;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.zotoh.core.crypto.BaseOfuscator;
import com.zotoh.core.util.JSONUte;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.impl.DefaultDeviceFactory;

/**
 * Helper functions to read/write data from the device config file.
 * 
 * @author kenl
 */
public enum MiscUte implements Vars {
;

    /**
     * @param top
     * @param dev
     * @return
     * @throws JSONException
     * @throws ClassNotFoundException
     */
    public static Class<?> getUserDevCZ(JSONObject top, String dev) throws JSONException, ClassNotFoundException {
        JSONObject a= getDevFacs(top);
        String cz=a.optString(dev);
        Class<?> z= null;
        
        if (!isEmpty(cz)) {
            z=loadClass(cz);
        }
        
        return z;
    }

    /**
     * @param appDir
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject loadConf(File appDir) throws IOException, JSONException {
        File pf= new File(new File( appDir, CFG), APPCONF);
        String str=readFile(pf, "utf-8");
        return JSONUte.read(str);        
    }
    
    /**
     * @param appDir
     * @param top
     * @throws IOException
     * @throws JSONException
     */
    public static void saveConf(File appDir, JSONObject top) throws IOException, JSONException {
        File pf= new File(new File( appDir, CFG), APPCONF);
        String str= JSONUte.asString(top);
        writeFile(pf, str, "utf-8");        
    }
    
    /**
     * @param top
     * @return
     * @throws JSONException
     */
    public static JSONObject getDevFacs(JSONObject top) throws JSONException {
        JSONObject a= top.optJSONObject(CFGKEY_DEVHDLRS);
        if (a==null) {
            a= new JSONObject();
            top.put(CFGKEY_DEVHDLRS, a);
        }
        return a;
    }
    
    /**
     * @param top
     * @return
     * @throws JSONException
     */
    public static JSONObject getDevs(JSONObject top) throws JSONException {
        JSONObject a= top.optJSONObject(CFGKEY_DEVICES);
        if (a==null) {
            a= new JSONObject();
            top.put(CFGKEY_DEVICES, a);
        }
        return a;
    }
    
    /**
     * @param top
     * @param dev
     * @return
     * @throws JSONException
     */
    public static boolean existsDevice(JSONObject top, String dev) throws JSONException {
    	if (DT_WEB_SERVLET.equals(dev)) { return false; }    	
        if ( DefaultDeviceFactory.getAllDefaultTypes().contains(dev)) {
            return true;
        }        
        return existsUserDevice(top,dev);
    }
    
    /**
     * @param top
     * @param dev
     * @return
     * @throws JSONException
     */
    public static boolean existsUserDevice(JSONObject top, String dev) throws JSONException {
        JSONObject a= getDevFacs(top);
        for (Iterator<?> keys = a.keys(); keys.hasNext();) {
            if (dev.equals( trim( nsb(keys.next())))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param keyFile
     * @throws IOException
     */
    public static void maybeSetKey(File keyFile) throws IOException {
        String s= keyFile.exists() ? asString(readBytes(keyFile)) : "";
        maybeSetKey(s);
    }

    /**
     * @param s
     */
    public static void maybeSetKey(String s) {
        byte[] bits= null;
        s=trim(s);
        
        if ( !isEmpty(s)) {
            
//            CoreUte.tlog().debug("MiscUte: APP.KEY = {}", s);
            
            if (s.startsWith("B64:")) {
                s=s.substring(4);
                bits=Base64.decodeBase64(s);
            }  else {
                bits= asBytes(s);
            }
        }
        
        if (bits != null) {
            BaseOfuscator.setKey(bits);
        }
        
        
    }
    
    
}
