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

import static com.zotoh.core.util.ProcessUte.*;

/**
 * Looping mechanism using a thread to implement a periodic timer.
 *
 * @author kenl
 */
public abstract class ThreadedTimer extends RepeatingTimer {

    private volatile boolean _readyToLoop, _tictoc;
    
    /**
     * @param mgr
     */
    protected ThreadedTimer(DeviceManager<?,?> mgr)     {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#onStart()
     */
    protected void onStart() throws Exception {        
        _readyToLoop= true;
        _tictoc=true;
        preLoop();
        schedule();
    }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#onStop()
     */
    @Override
    protected void onStop()    {
        _readyToLoop= false;
        _tictoc=false;
        endLoop();
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#schedule()
     */
    protected void schedule() {
        final ThreadedTimer me=this;
        asyncExec(new Runnable() {
            public void run() {
                while ( me.loopy())
                try {
                      me.onOneLoop();  
                }
                catch (Exception e) {
                    tlog().warn("",e) ;
                }
                return;
            }            
        });
        
    }

    /**
     * @return
     */
    protected boolean readyToLoop() {        return _readyToLoop;    }
    
    /**
     * @throws Exception
     */
    protected void preLoop() throws Exception {}
    
    /**
     * 
     */
    protected  void endLoop() {}

    /**
     * @throws Exception
     */
    protected abstract void onOneLoop() throws Exception;
    
    private boolean loopy()  {
        
        if (! _readyToLoop) {
            return false;            
        }
        
        if (_tictoc) {
            _tictoc=false;
            long delay = getDelayMillis();  
            if (delay > 0L)
            safeThreadWait(delay) ;            
        }
        else {
            safeThreadWait( getIntervalMillis() ) ;                        
        }
        
        return _readyToLoop;
    }
    
}
