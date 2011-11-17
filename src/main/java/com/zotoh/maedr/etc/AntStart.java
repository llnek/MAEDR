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
import static com.zotoh.core.util.CoreUte.getCWD;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.MetaUte.loadClass;
import static com.zotoh.core.util.StrUte.isEmpty;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Module;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.util.MiscUte;

/**
 * (Internal use only).
 *
 * @author kenl
 */
public class AntStart implements Vars {

    private static Logger _log=getLogger(AntStart.class);       
    public static Logger tlog() {  return _log;    }    
	
    
    /**
     * @param args
     */
    public static void main(String[] args) {

    	boolean dbg=false, b1=false, b2=false;
        String appdir="";
        File root;
        
        for (int i=0; i < args.length; ++i) {
            
            if ("-appdir".equals( args[i])) {
                appdir=args[i+1];
                ++i;
            } 
            else if ("remote-debug".equals(args[i])) {
                b1=true;
            }
            else if ("maedr-server".equals(args[i])) {
                b2=true;
            }
        }
        
        if (!isEmpty(appdir)) {
            root=new File(appdir);
        } else {
            root= getCWD();
        }
        
        dbg= b1 && b2;        
        try {
            File cfg= new File(root, CFG);
            if ( !cfg.exists() || !cfg.isDirectory() || !cfg.canRead()) {
                throw new Exception("AntStart: cannot locate config directory");
            }
            File p=new File(cfg, APPPROPS);
            if (!p.exists() || !p.canRead()) {
                throw new Exception("AntStart: cannot locate/read app.properties file");          
            }
            File m=new File(cfg, APPCONF);
            if (!m.exists() || !m.canRead()) {
                throw new Exception("AntStart: cannot locate/read app.conf file");          
            }
            File x=new File(cfg, "ant.xml");
            if (!x.exists() || !x.canRead()) {
                throw new Exception("AntStart: cannot locate/read/write ant.xml file");          
            }

            File kf= new File(new File( root, REALM), KEYFILE);
            MiscUte.maybeSetKey(kf);
            
            Properties props= new Properties();
            InputStream inp=null;
            try {
                inp=readStream(p);
                props.load(inp);            
            }
            finally {
                close(inp);
            }
            props.put(MANIFEST_FILE, ""); //niceFPath(m));
            props.put(APP_DIR, niceFPath(root));
            
            String script="", svc="";
            try {
                for (int i=0; i < args.length; ++i) {
                    if ("-runscript".equals( args[i] )) {
                        script= args[i+1];
                        break;
                    }
                    if ("-invoke".equals( args[i] )) {
                        svc= args[i+1];
                        break;
                    }
                }
            }
            catch (Throwable t) {}                            
            
            System.getProperties().put(ENG_PROPS, props) ;
            
            if (!isEmpty(script)) {
                launchScript(root, script, props);
            } 
            else if (!isEmpty(svc)) {
                launchSvc(root, svc, props);
            }
            else {
                Module<?,?> mm= Module.getPipelineModule();
                AppEngine<?,?> eng= mm.newEngine();
                eng.start(props);
                if (dbg) {
                    System.exit(0);
                }
            }
            
        }
        catch (Throwable t) {
        	System.out.println(t.getMessage()) ;
            //t.printStackTrace();
        }
    }
    
    private static void launchScript(File root, String script, Properties props) throws Exception {
        if (script.endsWith(".groovy")) {
        	runGroovyScript(root, script, props);
        }
    }

    private static void runGroovyScript(File cwd, String script, Properties props) throws Exception {
    	File dir=new File( cwd, "src/main/groovy");    	
    	GroovyScriptEngine gse = new GroovyScriptEngine(new String[]{
    					niceFPath(dir)
    	});

    	Binding binding = new Binding();
    	binding.setVariable("engineProperties", props);
    	gse.run(script, binding) ;
    }
        
    private static void launchSvc(File root, String svc, Properties props) throws Exception {
        Class<?> z= loadClass(svc);
        Object obj= z.getConstructor().newInstance();
        z.getMethod("run").invoke(obj);        
    }
    
}
