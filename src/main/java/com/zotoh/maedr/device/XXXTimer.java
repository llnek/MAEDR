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

import java.util.Date;

import org.json.JSONObject;

import static com.zotoh.core.util.CoreUte.*;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

/**
 * Base class for timer devices.
 * 
 * The set of properties:
 * 
 * <b>delaysecs</>
 * The activation of this timer is delayed by this value, default is 0
 * <b>when</>
 * The timer is activated on a specific date or datetime.  There are only 2 accepted format.
 * (1) yyyyMMdd , e.g. 20121223
 * (2) yyyyMMddTHH:mm:ss , e.g. 20120721T23:13:54
 * 
 * @see com.zotoh.maedr.device.Device
 * 
 * @author kenl
 * 
 */
public abstract class XXXTimer extends Poller  {
    
    private long _delayMillis;
    private Date _when;
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject attrs) throws Exception {
        
        super.inizWithProperties(attrs);
        // we force a delay, dont really want it to start too early
        int delay= Math.max( attrs.optInt("delaysecs", 1 ), 1 );
        String when= trim( attrs.optString("when"));
        String fmt;
        if (! isEmpty(when)) {
            if ( when.indexOf(":") > 0) {
                fmt = "yyyyMMdd'T'HH:mm:ss";
            }
            else {
                fmt= "yyyyMMdd" ;
            }
            setWhen( parseDate(when, fmt) );
        }
        else
        if (delay > 0)        {
            setDelayMillis(1000L * delay ) ;
        }
        
    }

    /**
     * @param mgr
     */
    protected XXXTimer(DeviceManager<?,?> mgr) {
        super(mgr);
        _delayMillis=0L;
        _when=null;
    }

    /**
     * @return
     */
    protected long getDelayMillis() {        return _delayMillis;    }
    
    /**
     * @return
     */
    protected Date getWhen() {        return _when;    }
    
    /**
     * @param d
     */
    protected void setDelayMillis(long d) {
        tstNonNegLongArg("delay-millis", d);
        _delayMillis=d;
    }
    
    /**
     * @param d
     */
    protected void setWhen(Date d) {
        _when=d;
    }

}







