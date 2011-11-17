/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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
 
package com.zotoh.maedr.impl;

import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.MetaUte.loadClass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.zotoh.maedr.core.DeviceFactory;
import com.zotoh.maedr.core.Vars;
import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.FilePicker;
import com.zotoh.maedr.device.MemDevice;
import com.zotoh.maedr.device.OneShotTimer;
import com.zotoh.maedr.device.PopIO;
import com.zotoh.maedr.device.RepeatingTimer;
import com.zotoh.maedr.device.TcpIO;
import com.zotoh.maedr.device.WebIO;
import com.zotoh.maedr.device.netty.NettpIO;
import com.zotoh.maedr.device.netty.RestIO;
import com.zotoh.maedr.device.netty.WebSockIO;

/**
 * The device factory for all built-in devices.
 *
 * @author kenl
 */
public class DefaultDeviceFactory<T,R> extends DeviceFactory<T,R> implements Vars {
    
    private static final String[] DEVS={
        DT_ONESHOT, DT_REPEAT, DT_HTTP
        , DT_HTTPS
        , DT_JMS, DT_TCP
        ,DT_FILE,  DT_WEBSOC, DT_JETTY, DT_ATOM, DT_REST
        ,DT_POP3, DT_MEMORY 
    };
    
    @SuppressWarnings("serial")
    private static final Map<String, Class<?>> _DEVMAP= new HashMap<String, Class<?>>() {{ 
        put(DT_ONESHOT, com.zotoh.maedr.device.OneShotTimer.class);
        put(DT_REPEAT, com.zotoh.maedr.device.RepeatingTimer.class);
        put(DT_HTTP, com.zotoh.maedr.device.netty.NettpIO.class);
        put(DT_JMS, com.zotoh.maedr.device.JmsIO.class);
        put(DT_TCP, com.zotoh.maedr.device.TcpIO.class);
        put(DT_FILE, com.zotoh.maedr.device.FilePicker.class);
        put(DT_JETTY, com.zotoh.maedr.device.JettyIO.class);
        put(DT_ATOM, com.zotoh.maedr.device.FeedIO.class);
        put(DT_REST, com.zotoh.maedr.device.netty.RestIO.class);
        put(DT_POP3, com.zotoh.maedr.device.PopIO.class);
        put(DT_WEBSOC, com.zotoh.maedr.device.netty.WebSockIO.class);
    }};

    /**
     * @return
     */
    public static Map<String, Class<?>> getDevCZMap() {
        return Collections.unmodifiableMap(_DEVMAP) ;
    }
    
    /**
     * @param type
     * @return
     */
    public static Class<?> getDevCZ(String type) {
        if ("https".equals(type)) { type= "http"; }
        return _DEVMAP.get(type);
    }
    
    /**
     * @return
     */
    public static Set<String> getAllDefaultTypes() {
        Set<String> rc= ST();
        rc.add(DT_WEB_SERVLET);
        for (int i=0; i < DEVS.length; ++i) { 
            rc.add( DEVS[i]); 
        }
        return rc;
    }
    
    /**
     * @param mgr
     */
    public DefaultDeviceFactory(DeviceManager<T,R> mgr) {
        super(mgr);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.core.DeviceFactory#onNewDevice(com.zotoh.maedr.device.DeviceManager, java.lang.String, org.json.JSONObject)
     */
    protected Device onNewDevice(DeviceManager<T,R> dm, String type, JSONObject deviceProperties) throws Exception {

//        String choice= trim(dm.getEngine().getProperties().getProperty(NIO_CHOICE)) ;
        Device dev= null;
//        boolean netty=true;        
//        if ("apache".equals(choice)) { netty=false; }

        if ( DT_WEB_SERVLET.equals(type))  { dev= new WebIO(dm); }

        if ( DT_ONESHOT.equals(type))  { dev= new OneShotTimer(dm); }
        if ( DT_REPEAT.equals(type)) { dev= new RepeatingTimer( dm ) ; }
        if ( DT_HTTPS.equals(type)) { 
            dev= new NettpIO( dm, true) ; //: new HttpIO( dm, true) ;
        }
        if ( DT_HTTP.equals(type)) { 
            dev=  new NettpIO( dm); // : new HttpIO( dm) ; 
        }
        if ( DT_WEBSOC.equals(type)) { dev= new WebSockIO( dm) ;  }
        if ( DT_JETTY.equals(type)) {  
    		dev= makeDev(dm, "com.zotoh.maedr.device.JettyIO") ;
        }
        if ( DT_TCP.equals(type)) { dev= new TcpIO( dm) ;  }
        if ( DT_JMS.equals(type)) {
        	dev= makeDev(dm, "com.zotoh.maedr.device.JmsIO") ;
    	}
        if ( DT_POP3.equals(type)) { dev= new PopIO( dm) ; }
        if ( DT_ATOM.equals(type)) { 
        	dev= makeDev(dm, "com.zotoh.maedr.device.FeedIO") ;
    	}
        if ( DT_REST.equals(type)) { dev= new RestIO( dm) ; }
        if ( DT_FILE.equals(type)) { dev= new FilePicker( dm) ; }
        if ( DT_MEMORY.equals(type)) { dev= new MemDevice( dm) ; }
        
        return dev; 
    }

    // we don't want to pull in jars unnecessarily
    private Device makeDev(DeviceManager<T,R> dm, String cz) throws Exception {
    	return (Device) loadClass(cz).getConstructor(DeviceManager.class).newInstance(dm);
    }
    
}
