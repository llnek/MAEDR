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

import static com.zotoh.core.util.CoreUte.getCWD;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.FileUte.isDirKosher;
import static com.zotoh.core.util.FileUte.isFileKosher;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.maedr.util.MiscUte.maybeSetKey;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Set;

import org.json.JSONObject;

import com.zotoh.core.util.JSONUte;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.AppDirError;
import com.zotoh.maedr.core.Vars;

/**
 * (Internal use only).
 *
 * @author kenl
 */
abstract class Cmdline implements Vars {

    private Logger ilog() {  return _log=getLogger(Cmdline.class);    }    
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    

    protected final ResourceBundle _rcb;    
    private final File _mhome, _cwd;
    
    /**
     * @param args
     * @throws Exception
     */
    public void evalArgs(String[] args) throws Exception {
        eval(args);
        System.out.println("");
    }
    
    /**
     * @param args
     * @throws Exception
     */
    protected abstract void eval(String[] args) throws Exception;
        
    /**
     * @return
     */
    public abstract Set<String> getCmds();
    
    /**
     * @param home
     * @param cwd
     */
    protected Cmdline(File home, File cwd) {
    	_rcb= AppRunner.getBundle();
        _cwd=cwd;
        _mhome=home;
    }

    /**
     * @return
     */
    protected ResourceBundle rcb() { return _rcb; }
    
    /**
     * @param target
     * @throws Exception
     */
    protected void _runTargetService(String target, String service) throws Exception {
        org.apache.tools.ant.Main.main(new String[]{
                "-Dservicepoint="+service,
                "-buildfile",
                getBuildFilePath(),
//                "-quiet",
                target
            });
    }
    
    /**
     * @param target
     * @param prop
     * @param value
     * @throws Exception
     */
    protected void runTargetExtra(String target, String prop, String value) throws Exception {
        org.apache.tools.ant.Main.main(new String[]{
				"-D"+prop+"="+value,
                "-buildfile",
                getBuildFilePath(),
//                "-quiet",
                target
            });
    }
    
    /**
     * @param target
     * @throws Exception
     */
    protected void runTargetInProc(String target) throws Exception {
        String[] args= new String[]{
                "-buildfile",
                getBuildFilePath(),
//                "-quiet",
                target
            };
        new AntMainXXX().startAnt(args);
    }  
    
    private static class AntMainXXX extends org.apache.tools.ant.Main {
        protected void exit(int exitCode) {
        }
        public void startAnt(String[] args) {
            super.startAnt(args, null, null);
        }
        public AntMainXXX() {}        
    }
    
    /**
     * @param target
     * @throws Exception
     */
    protected void runTarget(String target) throws Exception {
        org.apache.tools.ant.Main.main(new String[]{
                "-buildfile",
                getBuildFilePath(),
//                "-quiet",
                target
            });
    }
    
    /**
     * @return
     * @throws Exception
     */
    protected File getMaedrDir() throws Exception {
        return _mhome;
    }
    
    /**
     * @return
     * @throws Exception
     */
    protected File getCwd() throws Exception {
        return _cwd;
    }
    
    /**
     * @throws AppDirError
     * @throws IOException
     */
    protected void assertAppDir() throws AppDirError, IOException {
        File c=getCWD(),
        bin=new File(c, BIN),
        cfg=new File(c, CFG),       
        cf=new File(cfg, APPPROPS);
        
        //tlog().debug("AppRunner: cwd = " + c);
        
        boolean ok= isDirKosher(bin) &&
        isDirKosher(cfg) &&
        isFileKosher(cf);
        
        if (!ok) {
            throw new AppDirError();
        }
        
        cf= new File(new File(c, REALM), KEYFILE);
        maybeSetKey(cf);                
    }

    private String getBuildFilePath() throws Exception {
        return niceFPath(new File(new File(getCwd(), CFG), "ant.xml"));
    }

    /**
     * @return
     * @throws Exception
     */
    protected boolean isWebApp() throws Exception {
        return nsb(getAppMeta().optString("apptype")).equals(APPTYPE_WEB);
    }
    
    /**
     * @return
     * @throws Exception
     */
    protected String getAppLang() throws Exception {
        return nsb(getAppMeta().optString("lang"));
    }
    
    /**
     * @return
     * @throws Exception
     */
    protected JSONObject getAppMeta() throws Exception {
        JSONObject root= JSONUte.read(new File(new File(getCwd(),CFG), APP_META)) ;
        return root==null ? new JSONObject() : root;
    }
    
    
    
}

