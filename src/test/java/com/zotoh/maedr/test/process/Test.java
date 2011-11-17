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
 
package com.zotoh.maedr.test.process;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.ProcessUte;
import com.zotoh.maedr.process.ProcBaseEngine;
import com.zotoh.netio.SimpleHttpSender;

//import com.zotoh.maedr.core.AppEngine;


/**
 * @author kenl
 *
 */
public class Test {
    
    /**/
    public static void main(String[] args) {
        Properties props= new Properties();
        ProcBaseEngine eng = new ProcBaseEngine();
        InputStream inp= null;
        try {
            inp= new FileInputStream(
            "W:/home/unfuddle/java_projects/maedr/src/test/com/zotoh/maedr/test/test.properties");
            props.load(inp) ;

            Thread t= new Thread(new Runnable() {
                public void run() {
                 Test.client();   
                }                
            });
            t.setDaemon(true);
            t.start();
            
            eng.start(props);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            StreamUte.close(inp);
        }
    }
    
    private static void client() {
        ProcessUte.safeThreadWait(5000L);
        
        String[] args= new String[] {
                "-url", "http://lt-wow:8220/helloworld",
                "-key", "w:/zotoh.p12",
                "-pwd", "Password1",
                "-doc", "c:/temp/xulrunner-1.9.1.2.en-US.win32.zip"
               };
               SimpleHttpSender.main(args);
    }
}
