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

import static com.zotoh.core.io.StreamUte.close;
import static com.zotoh.core.io.StreamUte.readStream;
import static com.zotoh.core.io.StreamUte.writeFile;
import static com.zotoh.core.util.CoreUte.isWindows;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.strstr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.core.util.JSONUte;
import com.zotoh.maedr.core.CmdHelpError;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdAppOps extends Cmdline {

    /**
     * @param home
     */
    public CmdAppOps(File home, File cwd) {
        super(home, cwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("app");
        }};
    }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    protected void eval(String[] args) throws Exception {
        if (args==null || args.length < 2) {
            throw new CmdHelpError();
        }
        String s1= args[1], s2= args.length > 2 ? args[2] : "";
        boolean bg=false, web=false;
        
        if ("compile".equals(s1)) {
        	compile();
        }
        else if ("test".equals(s1)) {
            testCode();
        }
        else if ("ide/eclipse".equals(s1)) {
        	genEclipse();
		}
        else if ("bundle".equals(s1)) {
        	pack(s2);
        }
        else if ("debug".equals(s1)) {
            dbg(s2);
        }
        else if (s1.startsWith("run")) {
        	bg= "run/bg".equals(s1) ;
        	runScript(bg, s2);
        }
        else if (s1.startsWith("create")) {
        	web= "create/web".equals(s1);
        	create(web, s2);
        }
        else if (s1.startsWith("start")) {
        	bg= "start/bg".equals(s1) ;
        	launch(bg);
        }
        else if (s1.startsWith("invoke")) {
        	bg= "invoke/bg".equals(s1) ;
        	invoke(bg, s2);
        } else {
        	throw new CmdHelpError();
        }
                
    }
    
    private void dbg(String port) throws Exception {
        runTarget( isWindows() ? "run-dbg-app-w32" : "run-dbg-app-nix") ;
    }
    
    private void create(boolean web, String app) throws Exception {
    	if (isEmpty(app)) { throw new CmdHelpError(); }
    	new CmdApps( getMaedrDir(), getCwd())
    	.eval(new String[] { "create", app, (web?"/webapp": "")});
    }
    
    private void pack(String out) throws Exception {
    	if (isEmpty(out)) { throw new CmdHelpError(); }
    	assertAppDir();
    	
    	// make a copy of original ant.xml in the tmp dir
    	// coz that's the one which gets packaged.
        String s=rc2Str("com/zotoh/maedr/env/ant.xml", "utf-8");
        File fo= new File( new File( getCwd() , TMP ) , "/ant.orig.xml");
        writeFile(fo, s, "utf-8");
    	
    	boolean web=isWebApp();
    	fo= new File(out);
    	fo.mkdirs();
    	out=niceFPath(fo);
    	if (isWindows()) {
    		runTargetExtra( 
    						web ? bundleWebApp("bundle-webapp-w32",out) : "bundle-w32", 
    										ANTOPT_OUTDIR, out);
    	} else {
    		runTargetExtra( web ? bundleWebApp("bundle-webapp-nix",out) : "bundle-nix", 
    						ANTOPT_OUTDIR, out);    
    	}
    }
    
    private String bundleWebApp(String target, String outdir) throws Exception {
    	File cfg= new File( getCwd(), CFG);
    	JSONObject dev, devs, root;
    	InputStream inp=null;
    	
        try {
            root=JSONUte.read(inp=readStream(new File(cfg, APPCONF))) ;
            devs=root.optJSONObject(CFGKEY_DEVICES);
        } finally { close(inp); }
                
        String json, xml, proc="", key, type, jetty="";
        int cnt=0;
        for (Iterator<?> it=devs.keys(); it.hasNext(); ) {
            key=nsb( it.next());
            dev= devs.optJSONObject(key);
            type=dev.optString(CFGKEY_TYPE);
            if ( dev.has(DEV_STATUS) && dev.optBoolean(DEV_STATUS)==false) { continue; }
            if (!"jetty".equals(type)) { continue; }
            proc= dev.optString("processor");
            jetty=key;
            ++cnt;
        }
        if (cnt > 1) { throw new Exception("Too many Jetty device(s)"); }
        if (cnt==0) { throw new Exception("No Jetty device defined"); }
        dev= (JSONObject) devs.remove(jetty);
        xml= toWebXML(dev);
        dev= new JSONObject();
        dev.put(CFGKEY_TYPE, DT_WEB_SERVLET);
        dev.put("port", "0");
        dev.put("host", "");
        if (!isEmpty(proc)) {        dev.put("processor", proc) ; }
        devs.put(WEBSERVLET_DEVID, dev);         
        json= JSONUte.asString(root);
        
        File fo= new File(outdir, "webapps");
        fo.mkdirs();
        File t= new File(fo, TMP); 
        t.mkdirs();
        new File(fo, REALM).mkdirs();
        new File(fo, DB).mkdirs();
        File c=new File(fo, CFG); 
        c.mkdirs();
        new File(fo, LOGS).mkdirs();        

        FileUtils.copyFileToDirectory(new File(cfg, APPPROPS), c);
        writeFile(new File(c, APPCONF), json, "utf-8");        
    	writeFile(new File(t, "web.xml"), xml, "utf-8");    	
    	
    	return target;
    }
    
    private void testCode() throws Exception {
    	assertAppDir();
        runTarget("test-code");    	
    }
    
    private void compile() throws Exception {
        assertAppDir();
        runTarget("compile-code");      
    }
    
    private void launch(boolean bg) throws Exception {
    	assertAppDir();
        if (bg) {
            runTarget( isWindows() ? "run-app-bg-w32" : "run-app-bg-nix");
        }else {
            runTarget("run-app");
        }
    }

    private void runScript(boolean bg, String script) throws Exception {
    	tstEStrArg("script-file", script);
    	assertAppDir();
        if (bg) {
            runTargetExtra( isWindows() ? "run-script-bg-w32" : "run-script-bg-nix",  ANTOPT_SCRIPTFILE, script);
        }else {
            runTargetExtra("run-script", ANTOPT_SCRIPTFILE, script);
        }                	
    }
    
    private void invoke(boolean bg, String svc) throws Exception {
    	tstEStrArg("java.runnable-class", svc);
    	assertAppDir();
        if (bg) {
            runTargetExtra( isWindows() ? "run-svc-bg-w32" : "run-svc-bg-nix",  ANTOPT_SVCPOINT, svc);
        }else {
            runTargetExtra("run-svc", ANTOPT_SVCPOINT, svc);
        }                	
    }

    private String toWebXML(JSONObject jetty) throws Exception {
        String xml= rc2Str("com/zotoh/maedr/env/web.xml", "utf-8");
        String r= toServletFrag(jetty) + toFilterDefs(jetty);
        xml= strstr(xml, "<!-- INSERT CONTENT HERE -->", r);
        return xml;
    }
    
    private String toServletFrag(JSONObject jetty) throws Exception {
        String sn= "MAEDR Servlet";
        String s= "<servlet>\n\t<servlet-name>" + sn+ "</servlet-name>\n\t"
        +"<servlet-class>com.zotoh.maedr.device.WEBServlet</servlet-class>\n"
        +"\t<load-on-startup>1</load-on-startup>\n"
        +"</servlet>";              
        return s + "\n" + toServletMappings(jetty, sn);
    }
    
    private String toServletMappings(JSONObject jetty, String sn) throws Exception {
        StringBuilder b= new StringBuilder(512);
        String s;
        JSONArray urls= jetty.optJSONArray("urlpatterns");
        if ( urls != null) for (int i=0; i < urls.length(); ++i) {
            s= "<servlet-mapping>\n\t<servlet-name>" + sn + "</servlet-name>\n\t"
                            + "<url-pattern>" + urls.optString(i) + "</url-pattern>\n</servlet-mapping>\n";
            b.append(s);
        }
        return b.toString();
    }
    
    private String toFilterDefs(JSONObject jetty) throws Exception {
        StringBuilder b= new StringBuilder(512);
        String s, fn;
        JSONArray fils= jetty.optJSONArray("filters");
        JSONObject f;
        if (fils != null) for (int i=0; i < fils.length(); ++i) {    
            fn= "filter" + Integer.toString(i+1);
            f= fils.optJSONObject(i);
            s= "<filter>\n\t<filter-name>" + fn + "</filter-name>\n\t<filter-class>" + 
                    f.optString("class") + "</filter-class>"
            + toFilterParams( f.optJSONObject("params"))+"</filter>\n"
            + "<filter-mapping>\n\t<filter-name>" + fn+"</filter-name>\n\t"
            + "<url-pattern>"+ f.optString("urlpattern") + "</url-pattern>\n"
            + "</filter-mapping>\n";
            b.append(s);
        }
        
        return b.toString();
    }
    
    private String toFilterParams(JSONObject obj) throws Exception {
        StringBuilder b= new StringBuilder(512);
        String key;
        if (obj != null) for (Iterator<?> it= obj.keys(); it.hasNext();) {
            key=nsb( it.next());
            b.append(toInitParam(key, obj.optString(key)));
        }
        return b.toString();
    }
    
    private String toInitParam(String pn, String pv) {
        return "<init-param>\n\t<param-name>"
        + pn + "</param-name>\n\t<param-value>"
        + pv + "</param-value>\n</init-param>\n";
    }
    
    protected void genEclipse() throws Exception {
        assertAppDir();
        
        String lang=getAppLang();
        if ("groovy".equals(lang)) {
        }
        else if ("scala".equals(lang)) {            
        }
        else if ("java".equals(lang)) {
        }
        else {
            throw new Exception("Failed to generate eclipse project, language = " + lang);
        }        
        genEclipseProj(lang);
    }
    
    protected void genEclipseProj(String lang) throws Exception {
        File out, ec, cwd= getCwd(), home=getMaedrDir();
        ec= new File(cwd, ECPPROJ);
        ec.mkdirs();
        FileUtils.cleanDirectory(ec);
        String app=cwd.getName();
        StringBuilder sb;
        String str=rc2Str("com/zotoh/maedr/eclipse/"+lang+"/project.txt", "utf-8");
        str=strstr(str, "${APP.NAME}", app);
        str=strstr(str, "${"+lang.toUpperCase()+".SRC}", niceFPath(new File(cwd, "src/main/"+lang)));
        str=strstr(str, "${TEST.SRC}", niceFPath(new File(cwd, "src/main/test")));
        out= new File(ec, ".project");
        writeFile(out, str, "utf-8");
        str=rc2Str("com/zotoh/maedr/eclipse/"+lang+"/classpath.txt", "utf-8");
        sb= new StringBuilder(512);
        scanJars(new File(home, "lib"), sb);
        scanJars(new File(home, "thirdparty"), sb);
        scanJars(new File(home, "dist"), sb);
        scanJars(new File(cwd, "lib"), sb);
        scanJars(new File(cwd, "thirdparty"), sb);
        scanJars(new File(cwd, "dist"), sb);
        str=strstr(str, "${CLASS.PATH.ENTRIES}", sb.toString());
        out= new File(ec, ".classpath");
        writeFile(out, str, "utf-8");
    }
    
    private void scanJars(File dir, StringBuilder out) throws Exception {
    	File[] fs= dir.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}});
    	String p;
    	if (fs != null) for (int i=0; i < fs.length; ++i) {
    		p=niceFPath(fs[i]);
    		p="<classpathentry kind=\"lib\" path=\"" + p + "\"/>\n";
    		out.append(p);
    	}
    }
    
}


