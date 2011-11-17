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

/**
 * Blocks thread until a result is ready.  This is used in cases where NIO is not
 * wanted - used in conjuction with Apache http IO.
 *
 * @author kenl
 */
public class SyncWaitEvent extends WaitEvent {
    
    private final Object _lock= new Object();
    
    /**
     * @param e
     */
    public SyncWaitEvent(Event e)    {
        super(e);
    }

    /**
     * The number of milli-seconds to wait.
     * 
     * @param millisecs in millisecs.
     */
    public void timeoutMillis(long millisecs)     {
        
        tlog().debug("WaitEvent.timeout() - taking timeout (msecs) : {}" , millisecs);
        
        synchronized(_lock) {         
            try  {                    
                _lock.wait(millisecs);
            }
            catch (InterruptedException e)  {
                tlog().warn("WaitEvent interrupted", e);
            }
        }
        
    }

    /**
     * The number of seconds to wait.
     * 
     * @param secs interval in secs.
     */
    public void timeoutSecs(int secs)    {
        timeoutMillis( 1000L * secs);
    }

    /**
     * Undo the block, and continue.
     */
    public void resume()    {
        
        String s= toString();
        tlog().debug("SyncWaitEvent: {}.resume()" , s);
        synchronized(_lock)        {
            _lock.notifyAll();            
        }        
        tlog().debug("SyncWaitEvent: {}.continue()" , s );
        
    }

    /**
     * Continue and this is the result.
     * 
     * @param obj the result.
     */
    public void resumeOnEventResult(EventResult obj)    {
        setEventResult(obj);
        resume();
    }

        
}
