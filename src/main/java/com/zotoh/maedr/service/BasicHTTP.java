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

package com.zotoh.maedr.service;

import static com.zotoh.core.util.StrUte.nsb;

import java.net.URL;

 
/**
 * @author kenl
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class BasicHTTP<T> extends ServiceIO {

	/**
	 * @param pt
	 */
	protected BasicHTTP(int pt) {
		port(pt);
	}
	
    /**
     * @param socTimeoutMillis
     * @return
     */
    public T soctout(int socTimeoutMillis) {
        safePutProp("soctoutmillis", socTimeoutMillis);
        return (T) this;
    }
    
	
    /**
     * @param h
     * @return
     */
	public T host(String h) {
        safePutProp("host", h); 
        return (T)this;
    }
    
    /**
     * @param h
     * @return
     */
    public T serverkey(URL ptrToFile) {
        safePutProp("serverkey", ptrToFile.toExternalForm()); 
        return (T) this;
    }
    
    /**
     * @param pwd
     * @return
     */
    public T serverpwd(String pwd) {
        safePutProp("serverkeypwd", nsb(pwd)); 
        return (T) this;
    }
    
    /**
     * @param millis
     * @return
     */
    public T waitMillis(int millis) {
        safePutProp("waitmillis", millis); 
        return (T) this;
    }

    /**
     * @param threads
     * @return
     */
    public T workers(int threads) {
        safePutProp("workers", threads); 
        return (T) this;
    }
    
    /**
     * @param port
     * @return
     */
    public T port(int port) {
    	safePutProp("port", port);
    	return (T) this;
    }
    
    
}
