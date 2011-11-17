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

import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.asBytes;
import static com.zotoh.core.util.CoreUte.getCZldr;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.isWindows;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.CoreUte.rc2bytes;
import static com.zotoh.core.util.CoreUte.toFileUrl;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.strstr;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.JSONUte;
import com.zotoh.maedr.core.CmdHelpError;
import com.zotoh.maedr.core.Module;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdApps extends Cmdline {

    protected static final String[] DIRS= { 
        PATCH, BIN, REALM, CFG, TPCL, LOGS, DB, LIB, CLSS, SRC, DIST, TMP };
    
    /**
     * @param home
     * @param cwd
     */
    public CmdApps(File home, File cwd) {
        super(home, cwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("create");
        }};
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    protected void eval(String[] args) 
    				throws Exception {
        if (args==null || args.length < 2 
                || !"create".equals(args[0])) {
            throw new CmdHelpError();
        }
        
        boolean webapp= "/webapp".equals( args.length > 2 ? args[2] : "");
        String app= args[1];
        File base= getCwd();
        CmdLineSequence s=constructInput(app);
        Properties props= new Properties();
        props.put("name", app);
        s.start(props);
        if (s.isCanceled()) { return; }
        props.put("delegate", "DelegateImpl");       
        props.put("processor", "FlowImpl");       
//        props.put("task", "Task10001");       
        props.put("apptype", webapp?APPTYPE_WEB:APPTYPE_SVR);
        
        System.out.println("\n\n");
        System.out.format("%-20s%s\n", "Application name:" , props.get("name"));
        System.out.format("%-20s%s\n", "Package name:" , props.get("package"));
        System.out.format("%-20s%s\n", "Database:" ,  ((Boolean)props.get("storage"))?"yes":"no");
        System.out.format("%-20s%s\n", "Language Choice:" , props.get("lang"));
        System.out.println("\n");
        
        File appdir=create0( false,base, props.getProperty("name"));
        create1(appdir, "", props);
        create2(appdir, "", props);
        create3(appdir, props);
        create4(appdir, props);
        create5(appdir, props);
        create6(appdir);        
        if (webapp) {
            create7(appdir, props);
        }        
        create8(appdir, props);
        
        System.out.println("\n");
        System.out.println("Application was created successfully.");
        System.out.println("\n");
        
    }
    
    /**
     * Make all the 1st level sub-dirs under app-dir.
     *  
     * @param dir
     * @param app
     * @return
     * @throws Exception
     */
    protected File create0(boolean isSamples, File dir, String app) throws Exception {
        File appdir= new File(dir, app);
        if (!isSamples) System.out.println("Creating application directory: " + app);
        appdir.mkdir();
        for (int i=0; i < DIRS.length; ++i) {
            if (!isSamples) System.out.println("Creating sub-directory: " + DIRS[i]);
            new File(appdir, DIRS[i]).mkdirs();
        }       
        return appdir;
    }
        
    /**
     * Create the manifest file.
     * 
     * @param appdir
     * @param manifest
     * @param props
     * @throws Exception
     */
    protected void create1(File appdir, String manifest, Properties props) throws Exception {
        String s=rc2Str("com/zotoh/maedr/env/app.mf", "utf-8" );        
        File cfg=new File(appdir, CFG);
        File out= new File( niceFPath(cfg) + "/app.conf");
        if (!isEmpty(manifest)) { s= manifest; }
        String pv= fullClassNS(props, "processor", "");        
        s=strstr(s, "${PROCESSOR}", pv);
        writeFile(out, s, "utf-8");
        props.put("mf", out);
    }
     
    /**
     * @param appdir
     * @param appprops
     * @param props
     * @throws Exception
     */
    protected void create2(File appdir, String appprops, Properties props) throws Exception {
        boolean wdb= (Boolean) props.get("storage");
        File realm=new File(appdir, REALM);
        File cfg=new File(appdir, CFG);
//        File w= new File(appdir, TMP);
        File db= new File(appdir, DB);
//        File mf= (File)props.get("mf");
        String pcfg= niceFPath(cfg);
        String dfcz= Module.getPipelineModule().getDefDelegateClass();
        String s=rc2Str("com/zotoh/maedr/env/"+APPPROPS, "utf-8");
        if (!isEmpty(appprops)) {
            s=appprops;
        }
        String pwd= PwdFactory.getInstance().createRandomText(24);      
        writeFile( new File(realm, KEYFILE),
                "B64:"+Base64.encodeBase64URLSafeString(asBytes(pwd)), "utf-8");                
        
        String dg= fullClassNS(props, "delegate", dfcz);
        s= strstr(s, "${DELEGATE_CLASS}", dg);          
        s= strstr(s, "${MANIFEST_FILE}", ""); //niceFPath(mf));
        s= strstr(s, "${WORK_DIR}", ""); //niceFPath(w));
        s= strstr(s, "${DB_URL}", niceFPath(db));
        s=strstr(s, "${DB_FLAG}", (wdb ? "" : "#"));
        s=strstr(s, "${WANT_DB}", (wdb ? "true" : "false"));
        
        File out= new File( pcfg + "/" + APPPROPS);
        writeFile(out, s, "utf-8");
        
        s=rc2Str("com/zotoh/maedr/env/" + CLOUDDATA, "utf-8");
        out= new File( pcfg + "/" + CLOUDDATA);
        writeFile(out, s, "utf-8");

        // copy test server p12 file
        byte[] bits=rc2bytes("com/zotoh/maedr/env/test.p12", getCZldr());
        out= new File( pcfg + "/test.p12");
        writeFile(out, bits);
    }
    
    /**
     * @param appdir
     * @param props
     * @throws Exception
     */
    protected void create3(File appdir, Properties props) throws Exception {
        String cname= props.getProperty("delegate");
        String pk= props.getProperty("package");
        String lang= props.getProperty("lang");
        boolean wdb= (Boolean) props.get("storage");
        File out, f= new File(appdir, SRC+"/"+lang);
        File cfg= new File(appdir, CFG);
        File test= new File(appdir, TESTSRC+ "/" + lang + "/test");
        test.mkdirs();
        f.mkdirs();
        f=new File(f, strstr(pk, ".", "/")); 
        f.mkdirs();
        out= new File(f, cname+"."+lang);
        
        // deal with delegate file
        String s= rc2Str("com/zotoh/maedr/util/Delegate."+lang+".tpl", "utf-8");
        s=strstr(s, "${PACKAGE_ID}", pk);
        s=strstr(s, "${CLASS_NAME}", cname);
        // for scala ?
        s=strstr(s, "${STATEFUL_FLAG}", wdb ? "true" : "false");                                
        s=strstr(s, "${PROC_CLASS_NAME}", props.getProperty("processor"));        
        writeFile(out, s, "utf-8");
        
        // deal with processor file
        cname=props.getProperty("processor");
        out= new File(f, cname+"."+lang);
        s= rc2Str("com/zotoh/maedr/util/" + 
                (wdb? "StateP" : "NStateP") + "."+lang+".tpl", "utf-8");
        s=strstr(s, "${PACKAGE_ID}", pk);
        s=strstr(s, "${CLASS_NAME}", cname);
//        s=strstr(s, "${TASK_NAME}", props.getProperty("task"));
        writeFile(out, s, "utf-8");

        // add a mock runner for local debugging purpose
        s= rc2Str("com/zotoh/maedr/util/MockRunner" + "."+lang+".tpl", "utf-8");
        s=strstr(s, "${PACKAGE_ID}", pk);
        s=strstr(s, "${APP.DIR}", niceFPath(appdir));
        s=strstr(s, "${LOG4J.REF}", toFileUrl(new File(cfg, "log4j.properties")));        
        s=strstr(s, "${MANIFEST.FILE}", niceFPath(new File( cfg, APPPROPS)));
        out= new File(f, "MockRunner."+lang);
        writeFile(out, s, "utf-8");
        
        // add a junit test class
        s= rc2Str("com/zotoh/maedr/util/TestSuite" + "."+lang+".tpl", "utf-8");
        out= new File(test, "TestSuite."+lang);
        writeFile(out, s, "utf-8");
        
    }
     
    /**
     * Create the properties file.
     *  
     * @param appdir
     * @param props
     * @throws Exception
     */
    protected void create4(File appdir, Properties props) throws Exception {
        File cfg= new File(appdir, CFG);
        String s=rc2Str("com/zotoh/maedr/env/ant.xml", "utf-8");
        s= strstr(s, "${env.MAEDR_HOME}", niceFPath(getMaedrDir()));
        
        File out= new File( niceFPath(cfg) + "/ant.xml");
        writeFile(out, s, "utf-8");
    }
     
    /**
     * Create the log4j file.
     * 
     * @param appdir
     * @param props
     * @throws Exception
     */
    protected void create5(File appdir, Properties props) throws Exception {
//        File log= new File(appdir, LOGS);
        File cfg= new File(appdir, CFG);
        String s=rc2Str("com/zotoh/maedr/env/"+LOG4J, "utf-8" );
//        s= strstr(s, "${LOGS_DIR}", niceFPath(log));
        s= strstr(s, "${LOGS_DIR}", "./logs");
        
        File out= new File( cfg, LOG4J_PROPS);
        writeFile(out, s, "utf-8");
    }

    /**
     * @param appdir
     * @throws Exception
     */
    protected void create6(File appdir) throws Exception {
        String cmd="";
        if (isWindows()) {
        	//TODO
        }
        else {
            cmd= "chmod 700 " + niceFPath(  new File(appdir, REALM) );
            Runtime.getRuntime().exec(cmd);
            cmd= "chmod 600 " + niceFPath(  new File(new File(appdir, REALM),KEYFILE) );
            Runtime.getRuntime().exec(cmd);
        }
    }

    /**
     * @param appdir
     * @throws Exception
     */
    protected void create7(File appdir, Properties props) throws Exception {
        
        String s= rc2Str("com/zotoh/maedr/env/webapp.conf", "utf-8");
        String pkg= props.getProperty("package");
        String lang= props.getProperty("lang");
        
        new File(appdir, "webapps/WEB-INF/classes").mkdirs();
        new File(appdir, "webapps/WEB-INF/lib").mkdirs();
        new File(appdir, "webapps/images").mkdirs();
        new File(appdir, "webapps/scripts").mkdirs();
        new File(appdir, "webapps/styles").mkdirs();
        
        s=strstr(s, "${RESBASE}", niceFPath(new File(appdir, "webapps")));
        s=strstr(s, "${PROCESSOR}", pkg+"." + "WEBProcessor");        
        writeFile( new File( new File(appdir, CFG), APPCONF), s);
        
        File out, f= new File(appdir, SRC+"/"+lang);
        f.mkdirs();
        f=new File(f, strstr(pkg, ".", "/")); 
        f.mkdirs();
        
        out= new File(f, "WEBProcessor"+"."+lang);
        s= rc2Str("com/zotoh/maedr/util/WEBProc."+ lang + ".tpl", "utf-8");        
        s=strstr(s, "${PACKAGE_ID}", pkg);
        writeFile(out, s, "utf-8");
        
    }
    
    protected void create8(File appdir, Properties props) throws Exception {
        JSONObject obj= new JSONObject();
        String str, lang=props.getProperty("lang");
        obj.put("apptype", props.getProperty("apptype"));
        obj.put("lang", lang);
        str=JSONUte.asString(obj);
        writeFile( new File(new File(appdir, CFG), APP_META), str, "utf-8" );               

        new CmdAppOps(getMaedrDir(), appdir).genEclipseProj(lang);
    }
    
    private String fullClassNS(Properties props, String key, String df) {
        String pkg= props.getProperty("package");
        String pv=props.getProperty(key);
        if (isEmpty(pv)) {      
            pv=df;
        }  else {
            pv= pkg+"."+pv;
        }        
        return pv;
    }
    
    private CmdLineSequence constructInput(final String appdir) throws Exception {
        CmdLineQuestion q4= new CmdLineQuestion("lang", getResourceStr(rcb(),"cmd.which.lang"), "java/groovy/scala", "java") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("lang", answer);
                return "";
            }};
        CmdLineQuestion q3= new CmdLineQuestion("db", getResourceStr(rcb(),"cmd.use.db"), "y/n", "n") {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("storage", "Yy".indexOf(answer)>=0);
                return "lang";
            }};
        final CmdLineQuestion q2= new CmdLineQuestion("pkg", getResourceStr(rcb(),"cmd.package"), "", appdir) {
            protected String onAnswerSetOutput(String answer,
                    Properties props) {
                props.put("package", answer);
                return "db";
            }};
        final CmdLineQuestion q1= new CmdLineQuestion("app", getResourceStr(rcb(),"cmd.app.name"), "",appdir) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("name", answer);
                q2.setDefaultAnswer("com." + answer.toLowerCase());
                return "pkg";
            }};
        return new CmdLineSequence(q1,q2,q3,q4){
            protected String onStart() {
                return q2.getId();
            }           
        };

    }
    
    
    
}


