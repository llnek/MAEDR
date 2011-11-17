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

import static com.zotoh.core.util.CoreUte.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

/**
 * Base class for all polling devices.
 *
 * @author kenl
 */
public abstract class Poller extends Device {
    
    private TimerTask _timerTask;
    private Timer _timer;
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject deviceProperties) throws Exception {
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStart()
     */
    protected void onStart() throws Exception {    
        _timer= new java.util.Timer(true);
        _timerTask= new TimerTask() {
            public void run() {
                try {
                    wakeup();
                }
                catch (Throwable t) {
                    tlog().warn("",t);
                }
                return;                
            }
        };

        schedule();
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#onStop()
     */
    protected void onStop() {        
        if ( _timer != null) { _timer.cancel(); }
        _timer=null;
        _timerTask=null;                
    }
            
    /**
     * @param mgr
     */
    protected Poller(DeviceManager<?,?> mgr) {
        super(mgr);
    }
    
    /**
     * @param w
     */
    protected void scheduleTriggerWhen(Date w) {
        tstObjArg("date", w) ;
        if ( _timer != null) { 
            _timer.schedule( _timerTask, w);
        }
    }
    
    /**
     * @param delay
     */
    protected void scheduleTrigger(long delay) {
        if ( _timer != null) {
            _timer.schedule( _timerTask, (long) Math.max( 0L, delay ));
        }
    }
    
    /**
     * @param w
     * @param interval
     */
    protected void scheduleRepeaterWhen(Date w, long interval) {
        tstObjArg("when", w);
        if ( _timer != null) { 
            _timer.schedule( _timerTask, w, interval);
        }
    }
    
    /**
     * @param delay
     * @param interval
     */
    protected void scheduleRepeater(long delay, long interval) {
        tstPosLongArg("repeat-interval", interval) ;
        tstNonNegLongArg("delay", delay) ;
        if ( _timer != null) {  
            _timer.schedule( _timerTask, Math.max(0L, delay), interval);
        }
    }
    
    /**
     * 
     */
    protected abstract void schedule();
    
    /**
     * 
     */
    protected abstract void wakeup();
    
}
