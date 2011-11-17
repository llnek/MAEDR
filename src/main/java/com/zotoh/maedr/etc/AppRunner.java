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


import static com.zotoh.core.util.CoreUte.makeString;
import static com.zotoh.core.util.CoreUte.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.zotoh.core.util.CoreUte;
import com.zotoh.maedr.core.AppDirError;
import com.zotoh.maedr.core.CmdHelpError;
import com.zotoh.maedr.core.Vars;

/**
 * (Internal use only).
 *
 * @author kenl
 */
public enum AppRunner implements Vars {
;
	private static ResourceBundle _RCB;
	
//    private static Logger _log= getLogger(AppRunner.class); 
//    public static Logger tlog() {  return _log;    }    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    boolean cloud=false;
		try {
            for (int i=0; i < args.length; ++i) {
                if ( "cloud".equals( args[i])) { cloud=true; break; }
            }
		    if ( !parseArgs(args)) {
		        throw new CmdHelpError();
		    }
		}
		catch (AppDirError e) {
		    System.out.println("You must run the command in the application directory.");
		}
        catch (CmdHelpError e) {
            if ( cloud ) { usage_cloud(); } else  { usage(); }
        }
		catch (Throwable t) {
		    tlog().error("", t);
		    //t.printStackTrace();
            System.out.println(t.getMessage());
		}
	}

	/**/
    private static void usage_cloud() {
        System.out.println(makeString('=',78));
        System.out.println("> maedr <commands & options>");
        System.out.println("> cloud-related commands");
        System.out.println("> ----------------------");
        String[][] a=new String[][] {
        				
                {"cloud configure", "Set cloud info & credential."},
                {"cloud sshinfo", "Set SSH info."},
                
                {"cloud install <ver> <host:dir>", "Install MAEDR to host:target-dir."},
                {"cloud app/deploy  <host:dir>", "Deploy app to host:target-dir."},
                {"cloud app/run  <host:dir>", "Deploy and run app."},                
                
                {"cloud sync <regions|datacenters>", "Get latest set of Regions or Zones."},
                
                {"cloud image/set <image-id>", "Set default Image."},
                {"cloud image/*",  "Launch an Image."},
                
                {"cloud ip/list", "List Elastic IPAddrs."},
                {"cloud ip/bind <ipaddr> <vm-id>", "Assign IPAddr to VM."},
                {"cloud ip/+", "Add a new IPAddr."},
                {"cloud ip/- <ipaddr>", "Remove a IPAddr."},
                
                {"cloud vm/list", "List Virtual Machines."},
                {"cloud vm/set <vm-id>", "Set default VM."},
                {"cloud vm/? [vm-id]", "Describe a VM."},
                {"cloud vm/* [vm-id]", "Start a VM."},
                {"cloud vm/! [vm-id]", "Stop a VM."},
                {"cloud vm/% [vm-id]", "Terminate a VM."},
                
                {"cloud sshkey/list", "List SSH Keys."},
                {"cloud sshkey/set <keyname>", "Set default SSH Key."},
                {"cloud sshkey/+ <keyname>", "Add a new SSH Key."},
                {"cloud sshkey/- <keyname>", "Remove a SSH Key."},
                
                {"cloud secgrp/list", "List Security Groups."},
                {"cloud secgrp/set <group>", "Set default Security Group."},
                {"cloud secgrp/+ <group>", "Add a new Security Group."},
                {"cloud secgrp/- <group>", "Remove a Security Group."},
                
                {"cloud fwall/+ <group@rule>", "Add a new Firewall rule."},
                {"cloud fwall/- <group@rule>", "Remove a Firewall rule."},
                {":e.g. xyz@tcp#0.0.0.0/0#1#10", "From port 1 to port 10."},
                {":e.g. xyz@tcp#0.0.0.0/0#22", "Port 22."}
                
        };
        drawHelpLines("> %-35s\' %s\n", a);
        System.out.println(">");        
        System.out.println("> help - show standard commands");
        System.out.println("> help cloud - show commands related to cloud operations");
        System.out.println(makeString('=',78));
    }

    
    private static void drawHelpLines(String fmt, String[][] a) {
        String[] ss;
        for (int i=0; i < a.length; ++i) {
            ss= a[i];
            if (ss!=null)
            { System.out.format(fmt, ss[0], ss[1]); }
            else {
                System.out.println("");
            }
        }
    }
    
    
	private static void usage() {
        System.out.println(makeString('=',78));
	    System.out.println("> maedr <commands & options>");
        System.out.println("> standard commands");
        System.out.println("> -----------------");
        String[][] a= new String[][] {
                
                {"app create/web <app-name>",  "e.g. create helloworld as a webapp."},
                {"app create <app-name>",  "e.g. create helloworld"},
                
                {"app ide/eclipse", "Generate eclipse project files."},
                {"app compile", "Compile sources."},
                {"app test", "Run test cases."},
                
//                {"app invoke[/bg] <runnable>", "Invoke a Java Runnable object."},
                {"app debug <port>", "Start & debug the application."},
                {"app start[/bg]", "Start the application."},
                {"app run[/bg] <script-file>", "Run a Groovy script."},
                
                {"app bundle <output-dir>", "Package application."},
                
                {"device configure <device-type>", "Configure a device."},
                {"device add <new-type>", "Add a new  device-type."},
                
                {"crypto generate/serverkey", "Create self-signed server key (pkcs12)."},
                {"crypto generate/password", "Generate a random password."},
                {"crypto generate/csr", "Create a Certificate Signing Request."},                
                {"crypto encrypt <some-text>", "e.g. encrypt SomeSecretData"},
                {"crypto testjce", "Check JCE  Policy Files."},
                
                {"demo samples", "Generate a set of samples."},                
                {"version", "Show version info."}
        };
        drawHelpLines("> %-35s\' %s\n", a);
        System.out.println(">");        
        System.out.println("> help - show standard commands");
        System.out.println("> help cloud - show commands related to cloud operations");
        System.out.println(makeString('=',78));        
	}
	
	/**
	 * @return
	 */
	public static ResourceBundle getBundle() { return _RCB; }
	
	
	private static void inizBundle() {
		String[] ss= System.getProperty("maedr.locale", "en_US").split("_");
		String lang= ss[0];
		Locale loc;
		if (ss.length > 1) {
			loc=new Locale(lang, ss[1]);
		}else {
			loc=new Locale(lang);
		}
		
		//System.out.println("Locale= " + loc.toString());
		
		_RCB = CoreUte.getBundle("com/zotoh/maedr/i18n/AppRunner", loc);		
	}
	
	@SuppressWarnings("serial")
    private static boolean parseArgs(String[] args) throws Exception {
	    
	    if (args.length < 2) { return false; }

	    inizBundle();
	    
	    String home=StringUtils.stripEnd(niceFPath(new File(args[0])), "/");
	    final File h= new File(home);
	    final File cwd= getCWD();
	    List<Cmdline> cmds= new ArrayList<Cmdline>() {{ 
	        add(new CmdSamples(h,cwd));
	        add(new CmdCrypto(h,cwd));
	        add(new CmdCloud(h,cwd));
            add(new CmdDevice(h,cwd));
            add(new CmdAppOps(h,cwd));
            add(new CmdMiscOps(h,cwd));
	    }};
	    
	    for (Cmdline c : cmds) {
	        if ( c.getCmds().contains(args[1])) {
	            c.eval( Arrays.copyOfRange(args, 1, args.length));
	            return true;
	        }
	    }
	    
	    return false;
	}
	
}
