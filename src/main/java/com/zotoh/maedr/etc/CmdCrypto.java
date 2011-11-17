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
import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.StrUte.trim;
import static org.apache.commons.lang.time.DateUtils.addMonths;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.GUID;
import com.zotoh.core.util.Tuple;
import com.zotoh.crypto.Crypto;
import com.zotoh.crypto.Crypto.CertFormat;
import com.zotoh.maedr.core.CmdHelpError;

/**
 * (Internal use only).
 *
 * @author kenl
 */
class CmdCrypto extends Cmdline {

    /**
     * @param home
     * @param cwd
     */
    public CmdCrypto(File home, File cwd) {
        super(home, cwd);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#getCmds()
     */
    @SuppressWarnings("serial")
    public Set<String> getCmds() {
        return new HashSet<String>() {{ 
            add("crypto");
        }};
    }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.etc.Cmdline#eval(java.lang.String[])
     */
    protected void eval(String[] args ) throws Exception {
        if (args==null || args.length < 2) {
            throw new CmdHelpError();
        }
        
        String s2= args.length > 2 ? args[2] : "",          	
        //s0= args[0], 
        s1= args[1];
        
        if (s1.startsWith("generate/")) {
            generate(s1.substring(9));
        }
        else
        if ("encrypt".equals(s1)) {
            encrypt(s2);
        }
        else
        if ("testjce".equals(s1)) {
            testjce();
        }
        else {
            throw new CmdHelpError();
        }
                
    }
    
    private void testjce() throws Exception {
        try { 
            Crypto.testJCEPolicy();
            System.out.format("%s\n", getResourceStr(_rcb, "cmd.jce.ok"));
        } catch (Exception e) {
            System.out.format("%s\n%s\n%s\n%s\n", 
				getResourceStr(_rcb, "cmd.jce.error1"),                
				getResourceStr(_rcb, "cmd.jce.error2"),                
				getResourceStr(_rcb, "cmd.jce.error3"),                
				getResourceStr(_rcb, "cmd.jce.error4")) ;                
        }
    }
        
    private void encrypt(String txt) throws Exception {
        assertAppDir();
        System.out.println("\n" + PwdFactory.getInstance().create(txt).getAsEncoded());
    }
        
    /**/
    private void generate(String arg) throws Exception {
        File appdir= getCwd();

        if ("password".equals(arg)) {     
            generatePassword();
        }
        else 
        if ("serverkey".equals(arg)) {
            keyfile(appdir);
        }
        else 
        if ("csr".equals(arg)) {            
            csrfile(appdir);
        }
        else {
            throw new CmdHelpError();
        }
        
    }
    
    /**/
    private void keyfile(File appdir) throws Exception {      
        
        CmdLineSequence s=keyFileInput();
        Properties props= new Properties();
        s.start(props);        
        if (s.isCanceled()) { return; }
        
        String cn= trim(props.getProperty("cn"));
        String ou= trim(props.getProperty("ou"));
        String o=trim(props.getProperty("o"));
        String loc=trim(props.getProperty("l"));
        String st=trim(props.getProperty("st"));
        String c= trim(props.getProperty("c"));
        String fn= trim(props.getProperty("fn"));
        
        int mths= asInt(trim(props.getProperty("months")), 12);
        int size= asInt(trim(props.getProperty("size")),1024);
        String pwd= trim(props.getProperty("pwd"));
        
        Date start= new Date();
        File out= new File(fn);
        Date end= addMonths(start, mths);
        
        Crypto.getInstance().createSSV1PKCS12(GUID.generate(), start, end, 
                "CN="+cn+", OU="+ou+", O="+o+", L="+loc+", ST="+st+", C="+c, 
                pwd, size, out) ;
    }
    
    /**/
    private void csrfile(File appdir) throws Exception {      
        
        Properties props= new Properties();
        CmdLineSequence s=csrInput();
        s.start(props);
        if (s.isCanceled()) { return; }
        
        int size= asInt(trim(props.getProperty("size")),1024);
        String cn= trim(props.getProperty("cn"));
        String ou= trim(props.getProperty("ou"));
        String o=trim(props.getProperty("o"));
        String loc=trim(props.getProperty("l"));
        String st=trim(props.getProperty("st"));
        String c= trim(props.getProperty("c"));
        String fn= trim(props.getProperty("fn"));
                
        File out= new File(fn);        
        Tuple t=Crypto.getInstance().createCSR(size, 
                "CN="+cn+", OU="+ou+", O="+o+", L="+loc+", ST="+st+", C="+c, CertFormat.PEM);
        writeFile(out, (byte[]) t.get(0));
        
        out= new File(fn + ".key");        
        writeFile(out, (byte[]) t.get(1));
    }
    
    private void generatePassword() throws Exception {
        System.out.println("\n" + PwdFactory.getInstance().createRandomText(16));
    }
    
    // create the set of questions to prompt during the creation of server key
    private CmdLineSequence keyFileInput() throws Exception {
        CmdLineQuestion q10= new CmdLineQuestion("fname", getResourceStr(rcb(), "cmd.save.file"), "", "test.p12") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("fn", answer);
                return "";
            }};
        CmdLineQuestion q9= new CmdLineQuestion("pwd", getResourceStr(rcb(),"cmd.key.pwd")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("pwd", answer);
                return "fname";
            }};
        final CmdLineQuestion q8= new CmdLineQuestion("duration", getResourceStr(rcb(),"cmd.key.duration"), "", "12") {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("months", answer);
                return "pwd";
            }};
        CmdLineSequence p= csrInput();
        p.remove("fname");
        return new CmdLineSequence(p, q8,q9,q10){
            protected String onStart() {
                return q8.getId();
            }           
        };

    }
    

    // create the set of questions to prompt during the creation of CSR
    private CmdLineSequence csrInput() throws Exception {
        final CmdLineQuestion q8= new CmdLineQuestion("fname", getResourceStr(rcb(), "cmd.save.file"), "", "test.csr") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("fn", answer);
                return "";
            }};
        final CmdLineQuestion q7= new CmdLineQuestion("size", getResourceStr(rcb(), "cmd.key.size"), "", "1024") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("size", answer);
                return "fname";
            }};
        final CmdLineQuestion q6= new CmdLineQuestion("c", getResourceStr(rcb(), "cmd.dn.c"), "", "US") {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("c", answer);
                return "size";
            }};
        final CmdLineQuestion q5= new CmdLineQuestion("st", getResourceStr(rcb(),"cmd.dn.st")) {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("st", answer);
                return "c";
            }};
        final CmdLineQuestion q4= new CmdLineQuestion("loc", getResourceStr(rcb(),"cmd.dn.loc")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("l", answer);
                return "st";
            }};
        final CmdLineQuestion q3= new CmdLineQuestion("o", getResourceStr(rcb(),"cmd.dn.org"), "", "") {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("o", answer);
                return "loc";
            }};
        final CmdLineQuestion q2= new CmdLineQuestion("ou", getResourceStr(rcb(), "cmd.dn.ou")) {
            protected String onAnswerSetOutput(String answer,  Properties props) {
                props.put("ou", answer);
                return "o";
            }};
        final CmdLineQuestion q1= new CmdLineQuestion("cn", getResourceStr(rcb(), "cmd.dn.cn")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("cn", answer);
                return "ou";
            }};
        CmdLineSequence s= new CmdLineSequence(q1,q2,q3,q4,q5,q6,q7,q8){
            protected String onStart() {
                return "cn";
            }           
        };
        return s;
    }
    
    
    
}


