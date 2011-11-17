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
 
package com.zotoh.maedr.core;


/**
 * @author kenl
 *
 */
public interface Vars {
    
	//public static final String DEFDEG_CZ= "com.zotoh.maedr.impl.DefaultDelegate";
	
    public static final String APPPROPS= "app.properties";
    public static final String CLOUDDATA= "cloud.json";
    public static final String APPCONF= "app.conf";
    public static final String LOG4J= "log4j.txt";
    public static final String LOG4J_PROPS= "log4j.properties";
    
    public static final String SHUTDOWN_PORT_PWD= "maedr.shutdown.port.password";
    public static final String SHUTDOWN_PORT= "maedr.shutdown.port";
    public static final String DELEGATE_CLASS= "maedr.delegate.class";
    public static final String MANIFEST_FILE= "maedr.conf.file";
    public static final String PIPLINE_MODULE= "maedr.pipeline";
    public static final String APP_DIR= "maedr.app.dir";
    public static final String SECRET_KEY= "maedr.key";
    public static final String USE_CLDR= "maedr.use.classloader";
    public static final String NIO_CHOICE= "maedr.nio.choice";
    public static final String ENG_PROPS= "maedr.engine.props";
    public static final String WEBSERVLET_PROC= "maedr.servlet.processor";
    public static final String FILE_ENCODING= "maedr.file.encoding";
            
    public static final String PP_IMAGEID="cloud.image";
    public static final String PP_VMID="cloud.server";
    public static final String PP_REGION="cloud.region";
    public static final String PP_ZONE="cloud.zone";
    public static final String PP_SSHKEY="cloud.sshkey";
    public static final String PP_SECGRP="cloud.firewall";
    public static final String PP_PRODUCT="cloud.product";

    public static final String TDS_EVENTS= "maedr.core.event.tds";
    public static final String TDS_WORK= "maedr.core.work.tds";
    public static final String TDS_WAIT= "maedr.core.wait.tds";
    
    public static final String DT_ONESHOT= "oneshot-timer" ;
    public static final String DT_WEB_SERVLET= "web-servlet" ;
    public static final String DT_REPEAT= "repeat-timer" ;
    public static final String DT_JETTY= "jetty" ;
    public static final String DT_WEBSOC= "websocket" ;
    public static final String DT_HTTPS= "https" ;
    public static final String DT_HTTP= "http" ;
    public static final String DT_TCP= "tcp" ;
    public static final String DT_REST= "rest" ;
    public static final String DT_JMS= "jms" ;
    public static final String DT_FILE= "filepicker" ;    
    public static final String DT_POP3= "pop3" ;    
    public static final String DT_ATOM= "atom" ;    
    public static final String DT_MEMORY= "in-memory";
    
    public static final String XML_ROOT= "maedr";
    public static final String STATE_TABLE= "MAEDR_STATE_INFO";

    public static final String SYS_DEVID_PFX= "system.####";
    public static final String SYS_DEVID_SFX= "####";
    
    public static final String SYS_DEVID_REGEX= SYS_DEVID_PFX+"[0-9A-Za-z_\\-\\.]+"+SYS_DEVID_SFX;
    public static final String SHUTDOWN_DEVID= SYS_DEVID_PFX+"kill_9"+SYS_DEVID_SFX;
    public static final String SHUTDOWN_URI="/kill9";
    
    public static final String WEBSERVLET_DEVID= "____in_a_webservlet____";
    public static final String INMEM_DEVID= "____in_memory____";
    public static final String DEVID= "id";
    public static final String DEV_STATUS= "enabled";
    public static final String DEV_PROC= "processor";
 
    public static final String WORK_DIR="maedr.work.dir"; 
    public static final String JDBC_POOLSIZE="maedr.db.poolsize";
    public static final String JDBC_DRIVER="maedr.db.driver";
    public static final String JDBC_URL="maedr.db.url";
    public static final String JDBC_USER="maedr.db.user";
    public static final String JDBC_PWD="maedr.db.pwd";
    public static final String JDBC_RESET="maedr.db.reset";
    
    public static final String CFGKEY_DEVHDLRS="devicehandlers";
    public static final String CFGKEY_DEVICES="devices";
    public static final String CFGKEY_CORES="cores";

    public static final String CFGKEY_DEV_IMPL="device-impl-class";
    //public static final String CFGKEY_FACTORY="factory";
    public static final String CFGKEY_TYPE="type";
    public static final String CFGKEY_THDS="threads";
    
    public static final String CFGKEY_SOCTOUT="soctoutsecs";
    public static final String CFGKEY_HOST="host";
    public static final String CFGKEY_PORT="port";
    
    
    public static final String DB_STATE_TBL="MAEDR_STATE_INFO";
    public static final String COL_BIN="BININFO";
    public static final String COL_KEYID="KEYID";
    public static final String COL_TRACKID="TRACKID";
    public static final String COL_EXPIRY="EXPIRYTS";
    
    public static final String SAMPLES= "samples";
    public static final String REALM= ".vault";
    public static final String KEYFILE= ".appkey";
    public static final String APP_META= ".meta";
    public static final String PROCID= "pid";
    
    public static final String APPTYPE_WEB="webapp";
    public static final String APPTYPE_SVR="server";

    public static final String ANTOPT_SVCPOINT="maedr.servicepoint";
    public static final String ANTOPT_SCRIPTFILE="maedr.scriptfile";
    public static final String ANTOPT_OUTDIR="maedr.useroutdir";
    
    public static final String TESTSRC= "src/test";
    public static final String SRC= "src/main";
    public static final String CLSS= "classes";
    public static final String DIST= "dist";
    public static final String TPCL= "thirdparty";
    public static final String BIN= "bin";
    public static final String CFG= "cfg";
    public static final String LOGS= "logs";
    public static final String DB= "db";
    public static final String LIB= "lib";
    public static final String TMP= "tmp";
    public static final String PATCH= "patch";
    public static final String ECPPROJ="eclipse.projfiles";
}
