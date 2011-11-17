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
 
package com.zotoh.maedr.device;

import java.util.Timer;
import java.util.TimerTask;

import static com.zotoh.core.util.CoreUte.*;

/**
 * For Asynchronous event handling.  This class wraps the actual event and will <b>wait</b> for the downstream
 * application until the response is ready.  In case the application takes too long to respond, a timer is set and a
 * time out will occur upon expiry.
 *
 * @author kenl
 */
public class AsyncWaitEvent extends WaitEvent {
    
    private final AsyncWaitTrigger _trigger;
    private Timer _timer;
    private TimerTask _task;
    
    /**
     * @param ev An event generated from a Device.
     * @param t A trigger which will be used to resume and respond back to caller.
     */
    public AsyncWaitEvent(Event ev, AsyncWaitTrigger t)    {    	
        super(ev);
        _trigger= t;
        tstObjArg("async-trigger",t);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.WaitEvent#resumeOnEventResult(com.zotoh.maedr.device.EventResult)
     */
    @Override
    public void resumeOnEventResult(EventResult res)    {
        
        if (_timer != null) 
        	{	_timer.cancel(); }
        
        getInnerEvent().getDevice().releaseEvent(this) ;
        setEventResult(res) ;        
    	_trigger.resumeWithResult(res) ;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.WaitEvent#timeoutMillis(long)
     */
    @Override
    public void timeoutMillis(long millisecs)     {
    	tstNonNegLongArg("timeout#millisecs", millisecs);
        final AsyncWaitEvent me= this;
        _timer = new Timer(true);
        _task= new TimerTask() {
            public void run() {
                me.onExpiry();
        }};        
        _timer.schedule(_task, millisecs) ;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.WaitEvent#timeoutSecs(int)
     */
    @Override
    public void timeoutSecs(int secs)    {
        timeoutMillis(1000L * secs) ;
    }

    private void onExpiry() {        
        getInnerEvent().getDevice().releaseEvent(this) ;        
        _trigger.resumeWithError() ;
    }
    
}
