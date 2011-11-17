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


package com.zotoh.maedr.device;

import static com.zotoh.core.util.CoreUte.tstNonNegIntArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;
import static com.zotoh.core.util.LangUte.MP;

import java.util.Map;

import org.json.JSONObject;


/**
 * Base class for all http related devices.  All http devices are by default *async* in nature.
 * 
 * The set of properties:
 * 
 * <b>soctoutmillis</b>
 * Socket timeout in milliseconds - default is 0
 * <b>workers</b>
 * No. of worker threads allocated for this device - default is 6.
 * <b>thresholdkb</b>
 * The upper limit on payload size before switching to file storage -default is 8Meg.
 * This is especially important when dealing with large http-payloads.
 * <b>waitmillis</b>
 * The time the device will *wait* for the downstream application to hand back a result.  If a timeout occurs, a timeout error will be sent back to
 * the client.  default is 5 mins.
 *  
 * @see com.zotoh.maedr.device.HttpIOTrait
 * 
 * @author kenl
 */
public abstract class BaseHttpIO extends HttpIOTrait {

    private long _thsHold, _waitMillis, _socTOutMillis;
    private final Map<Object,NIOCB> _cbs= MP() ;    
    private boolean _async=true;
    private int _workers ;    
    //private CryptoStore _cs;
        
    /**
     * @return
     */
    public boolean isAsync() {  return _async;    }
    
    /**
     * @return The upper limit on the size of payload before switching to use file based storage.
     */
    public long getThreshold() {  return _thsHold;    }
    
    
    /**
     * @return The number of milli-seconds to wait for the downstream application to come back with a result.
     */
    public long getWaitMillis() {  return _waitMillis;    }
        
    /**
     * @return Socket time out in millisecs.
     */
    public long getSocetTimeoutMills() {
        return _socTOutMillis;
    }
        
    /**
     * @return The number of inner worker threads for this IO device. 
     */
    public int getWorkers() {   return _workers;  }
    
           
    /**
     * @param mgr
     * @param ssl
     */
    protected BaseHttpIO( DeviceManager<?,?> mgr, boolean ssl ) {
        super(mgr, ssl);
    }
    
    /**
     * @param mgr
     */
    protected BaseHttpIO(DeviceManager<?,?> mgr) {
        this( mgr, false) ;
    }
    
    /**
     * @param key
     * @return
     */
    public NIOCB getCB(Object key) { return key==null ? null : _cbs.get(key); }

    /**
     * @param key
     * @param cb
     */
    public void addCB(Object key, NIOCB cb) {
    	tstObjArg("niocb", cb);
    	tstObjArg("key", key);
    	_cbs.put(key, cb);
    }
    
    /**
     * @param key
     * @return
     */
    public NIOCB removeCB(Object key) {
    		return key==null ? null : _cbs.remove(key) ;
    }
    
    /**/
    protected void inizWithProperties(JSONObject deviceProperties) 
    				throws Exception {        
		super.inizWithProperties(deviceProperties) ;
    		
        int socto= deviceProperties.optInt("soctoutmillis", 0);    // no timeout
        boolean nio =deviceProperties.optBoolean("async") ;
        int wks= deviceProperties.optInt("workers", 6);
        
        int thold= deviceProperties.optInt("thresholdkb", 8*1024);  // 8 Meg
        int wait= deviceProperties.optInt("waitmillis", 300000) ;   // 5 mins
        
        tstNonNegIntArg("socket-timeout-millis", socto) ;
        _socTOutMillis = 1L * socto;
        
        tstNonNegIntArg("threshold", thold) ;
        _thsHold = 1024L * thold;
        
        tstPosIntArg("wait-millis", wait) ;
        _waitMillis = 1L *wait;
                
        tstPosIntArg("workers", wks) ;
        _workers = wks;

        if (deviceProperties.has("async") && nio==false) {
            _async=false;
        }
        
    }
            
    /**
     * @author kenl
     *
     */
    public static abstract class NIOCB {
        public abstract void destroy();
        protected NIOCB()  {}
    }
    
    
}

