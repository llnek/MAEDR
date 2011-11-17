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

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * @author kenl
 *
 */
public enum SSHUte {
;

    /**
     * @param host
     * @param port std port = 22
     * @param user
     * @param pwd
     * @param key
     * @param data
     * @param remoteFile
     * @param remoteDir
     * @param mode
     * @throws Exception
     */
    public static void scp(String host, int port, String user, String pwd, String key
            , byte[] data, String remoteFile, String remoteDir, String mode) throws Exception {
        Connection conn= new Connection(host, port);        
        boolean ok;
        try {
            conn.connect();
            if (! isEmpty(key)) {
                ok= conn.authenticateWithPublicKey(user, new File(key), pwd) ;
            } else {
                ok= conn.authenticateWithPassword(user, pwd) ;
            }
            if (!ok) {
                throw new Exception("SSH: Cant authenticate with host: " + host);
            }            
            new SCPClient(conn).put(data, remoteFile, remoteDir, mode);            
        }
        finally {
            if (conn != null) { conn.close(); }
        }
    }

    /**
     * @param host
     * @param port std port = 22.
     * @param user
     * @param pwd
     * @param key
     * @param remoteDir
     * @param localFile
     * @param mode
     * @throws Exception
     */
    public static void scp(String host, int port, String user, String pwd, String key
    	            , String remoteDir, File localFile, String mode) throws Exception {
        Connection conn= new Connection(host, port );        
        boolean ok;
        try {
            conn.connect();
            
            if (! isEmpty(key)) {
                ok= conn.authenticateWithPublicKey(user, new File(key), pwd) ;
            } else {
                ok= conn.authenticateWithPassword(user, pwd) ;
            }
            
            if (!ok) {
                throw new Exception("SSH: Cant authenticate with host: " + host);
            }            
            new SCPClient(conn).put(niceFPath(localFile), remoteDir, mode);            
        }
        finally {
            if (conn != null) { conn.close(); }
        }
    }
    
    /**
     * @param delete
     * @param host
     * @param user
     * @param pwd
     * @param key
     * @param remoteFile
     * @param remoteDir
     * @return
     * @throws Exception
     */
    public static boolean rexec(boolean delete, String host, String user, String pwd, String key
            , String remoteFile, String remoteDir, String testString) throws Exception {
        
        Connection conn= new Connection(host);
        Session sess=null;
        boolean success=false, ok;
        try {
            conn.connect();
            
            if (! isEmpty(key)) {
                ok= conn.authenticateWithPublicKey(user, new File(key), pwd) ;
            } else {
                ok= conn.authenticateWithPassword(user, pwd) ;
            }
            if (!ok) {
                throw new Exception("SSH: Cant authenticate with host: " + host);
            }            

            sess = conn.openSession();
            sess.execCommand("cd " + remoteDir + " && ./" + remoteFile );
            
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true)            {
                String line = br.readLine();
                if (line == null)
                break;
                System.out.println(trim(line));
                if ( line.indexOf(testString) >=0 ) { success=true; }
            }
            sess.close();
            
            sess = conn.openSession();
            sess.execCommand("rm -f  " + remoteDir + "/" + remoteFile );
        }
        finally {
            if (sess != null) { sess.close(); }
            if (conn != null) { conn.close(); }
        }
        
        return success;
    }

        
    /**
     * @param host
     * @param user
     * @param pwd
     * @param key
     * @param remoteFile
     * @param remoteDir
     * @return
     * @throws Exception
     */
    public static boolean rdelete(String host, String user, String pwd, String key
    	            , String remoteFile, String remoteDir) throws Exception {
    	        
        Connection conn= new Connection(host);
        Session sess=null;
        boolean success=false, ok;
        try {
            conn.connect();
            if (! isEmpty(key)) {
                ok= conn.authenticateWithPublicKey(user, new File(key), pwd) ;
            } else {
                ok= conn.authenticateWithPassword(user, pwd) ;
            }
            if (!ok) {
                throw new Exception("SSH: Cant authenticate with host: " + host);
            }            

            sess = conn.openSession();
            sess.execCommand("rm -f " + remoteDir + " /" + remoteFile );
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true)            {
                String line = br.readLine();
                if (line == null)
                break;
                System.out.println(trim(line));
            }            
        }
        finally {
            if (sess != null) { sess.close(); }
            if (conn != null) { conn.close(); }
        }
        
        return success;
    }






}    

