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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.zotoh.core.util.StrUte.*;
import static com.zotoh.core.io.StreamUte.*;
import com.zotoh.maedr.core.CmdHelpError;

/**
 * @author kenl
 *
 */
public class CmdMiscOps extends Cmdline {

    /**
     * @param home
     * @param cwd
     */
    public CmdMiscOps(File home, File cwd) {
        super(home, cwd);
    }

    @SuppressWarnings("serial")
    @Override
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("package-app");
            add("version");
            add("deploy-app");
        }};
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    @Override
    protected void eval(String[] args) throws Exception {
        if (args==null || args.length < 1) {
            throw new CmdHelpError();
        }
        
        if ("version".equals(args[0])) {           
        	showVersion();
        }
        else if ("package-app".equals(args[0])) {           
            runTarget("packzip-app");            
        }
        else {
            throw new CmdHelpError();            
        }
    }

    private void showVersion() throws Exception {
    	File f=new File(getMaedrDir(), "VERSION");
    	if ( ! f.canRead()) { return; }
    	String s= readFile(f, "utf-8");
    	if (isEmpty(s)) { s= "???"; }
    	System.out.println(s);
    }
    
    
    
    
    
}
