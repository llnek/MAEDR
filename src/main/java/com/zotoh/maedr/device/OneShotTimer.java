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
import java.util.Properties;
import java.util.ResourceBundle;

import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import static com.zotoh.core.util.CoreUte.*;

/**
 * A timer which only fires once.
 * 
 * The set of properties:
 * 
 * @see com.zotoh.maedr.device.XXXTimer
 * 
 * @author kenl
 * 
 */
public class OneShotTimer extends XXXTimer {
    
    /**
     * @param mgr
     */
    public OneShotTimer(DeviceManager<?,?> mgr) {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#wakeup()
     */
    protected void wakeup() {        
        try {
            dispatch( new TimerEvent( this));
        }
        finally {
            stop();
        }
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#schedule()
     */
    protected void schedule() {
        
        Date w= getWhen();
        
        if (w == null)        {
            scheduleTrigger( getDelayMillis() );
        }  else   {
            scheduleTriggerWhen(w);
        }
        
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {
        final CmdLineQuestion q1= new CmdLineQuestion("delay", getResourceStr(rcb,"cmd.delay.start"), "","0") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("delaysecs", asInt(answer,0));
                return "";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }

}
