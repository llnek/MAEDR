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

import static com.zotoh.core.io.StreamUte.readFile;
import static com.zotoh.core.util.CoreUte.isWindows;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.rc2Str;
import static com.zotoh.core.util.StrUte.strstr;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.zotoh.core.io.StreamUte;
import com.zotoh.maedr.core.CmdHelpError;
import com.zotoh.maedr.core.Module;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdSamples extends CmdApps {

    private static final String[] SPLDIRS= { 
        "delegate", "atom", "file", "jetty", 
        //"web", 
        "stockquote", "multistep",
        "async", "fork", "http", "jms", "ssl", "pop3", "stateful", "tcpip", "timer", "rest", "websock" };
        
    /**
     * @param home
     * @param cwd
     */
    public CmdSamples(File home, File cwd) {
        super(home, cwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.CmdApps#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("demo");
        }};
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.CmdApps#eval(java.lang.String[])
     */
    protected void eval(String[] args) throws Exception {
        if (args==null || args.length < 1
                || !"samples".equals(args[1])
                || !"demo".equals(args[0]) ) {
            throw new CmdHelpError();
        }
        Module<?,?> mod= Module.getPipelineModule();
        String shortmn=mod.getShortName();
        String mn=mod.getName();
        String lang= args.length > 2 ? args[2] : "java";        
        File a, sample= new File("sample_apps");
        sample.mkdirs();
        String ptr, ms;
        Properties ps;
        File demo_src=new File(getMaedrDir(), "samples");
        File tp;
        String appprops=rc2Str("com/zotoh/maedr/env/"+APPPROPS, "utf-8");

        System.out.println("\n\n");
        System.out.println("Creating samples pack...");
        System.out.println("\n");
        
        for (int i=0; i < SPLDIRS.length; ++i) {
            
            ps= new Properties();
            ps.put("apptype", APPTYPE_SVR);
            ps.put("storage", false);
            ptr=SPLDIRS[i];
            
            System.out.format("%-24s%s\n", "Creating sample: ", ptr);
            
            a=create0(true, sample, ptr);
            ms=readFile(new File(new File(demo_src, ptr), "app.mf"), "utf-8");
            ms=strstr(ms, "${PIPELINE}", shortmn);
            ms=strstr(ms, "${PREAMBLE}",
            				"scala".equals(lang) ? "Preamble" :	"$Preamble");
            if ("stateful".equals(ptr)) {
                ps.put("storage", true);            	
            }
            else
            if ("ssl".equals(ptr)) {
                tp= new File(new File(a, CFG), "test.p12");
                ms=strstr(ms, "$KEY.P12",
                                (isWindows() ? "file:/" : "file:")+niceFPath(tp));
            }
            else
            if ("file".equals(ptr)) {
                tp= new File(new File(a, TMP), "infiles");
                tp.mkdirs();
                ms=strstr(ms, "$FILEPICK_SRC", niceFPath(tp));
                tp= new File(new File(a, TMP), "cache");
                tp.mkdirs();
                ms=strstr(ms, "$FILEPICK_DES", niceFPath(tp));
            }
            else
            if ("websock".equals(ptr)) {
                FileUtils.copyFileToDirectory(new File(new File(demo_src, ptr),"squarenum.html"), new File(a, CFG));
                FileUtils.copyFileToDirectory(new File(new File(demo_src, ptr),"jquery.js"), new File(a, CFG));
            }
            else
            if ("jetty".equals(ptr) || "web".equals(ptr)) {
                ps.put("apptype", APPTYPE_WEB);                
                String bs="assets";
                File ss=new File(a, "webapps/scripts");ss.mkdirs();
                File ii=new File(a, "webapps/images");ii.mkdirs();
                File cc=new File(a, "webapps/styles");cc.mkdirs();
                FileUtils.copyFileToDirectory(new File(new File(demo_src, bs),"favicon.ico"), ii);
                FileUtils.copyFileToDirectory(new File(new File(demo_src, bs),"test.js"), ss);
                FileUtils.copyFileToDirectory(new File(new File(demo_src, bs),"main.css"), cc);
                if ("web".equals(ptr)) {
                    File inf=new File(a, "webapps/WEB-INF");inf.mkdirs();
                    new File(a, "webapps/WEB-INF/classes").mkdirs();
                    FileUtils.copyFileToDirectory(new File(new File(demo_src, ptr),"web.xml"), inf);                    
                }
                ms=strstr(ms, "$RESBASE", new File(a,"webapps").toURI().toURL().toExternalForm());
            }
            else
            if ("pop3".equals(ptr)) {
                appprops= "maedr.pop3.mockstore=com.zotoh.maedr.mock.mail.MockPop3Store\n" 
                                + appprops;
            }
            
            create1(a, ms, ps);   
            
            ps.put("package", "demo."+ptr);
            ps.put("delegate", "");
            if ("delegate".equals(ptr)) {            	
                ps.put("delegate", mn+"Delegate");
            }            
            ps.put("lang", lang);
            
            create2(a, appprops, ps);            
            create3s(a, demo_src, ptr, ps);            
            create4(a, ps);            
            create5(a, ps);            
            create6(a);          
            create8(a, ps);
        }
        
        System.out.println("");
        System.out.println("All samples created successfully.");
        System.out.println("");
        
    }
    
    /**/
    private void create3s(File appdir, File src, String ptr, Properties props) throws Exception {
        File f, t= new File(appdir, SRC);
        String s, lang=props.getProperty("lang");
        File j= new File(t, lang +"/demo/"+ptr);
        j.mkdirs();        
        FileUtils.copyDirectory(new File(src, ptr+"/"+lang), j, 
                new NotFileFilter(new SuffixFileFilter(".mf")));
        Iterator<File> it=FileUtils.iterateFiles(j, new SuffixFileFilter("."+lang), null);
        while (it.hasNext()) {
        	f=it.next();
        	s=StreamUte.readFile(f, "utf-8");
        	s=strstr(s, "demo."+ptr+"."+ lang, "demo."+ptr);
        	StreamUte.writeFile(f, s, "utf-8");
        }
    }    
    
}


