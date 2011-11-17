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

import static com.zotoh.core.util.CoreUte.asInt;
import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstPosIntArg;

import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;

/**
 * Sets up a repeatable timer.
 * 
 * The set of properties: 
 * 
 * <b>intervalsecs</b>
 * The number of seconds between each trigger, default is 60.
 * 
 * @see com.zotoh.maedr.device.XXXTimer
 * 
 * @author kenl
 * 
 */
public class RepeatingTimer extends XXXTimer  {
    
    private long _intervalMillis= 0L;
        
    /**
     * @param mgr
     */
    public RepeatingTimer(DeviceManager<?,?> mgr) {
        super(mgr);
    }
    
    /**
     * @return
     */
    public long getIntervalMillis() {        return _intervalMillis;    }
            
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.XXXTimer#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject attrs) throws Exception {
        super.inizWithProperties(attrs) ;
        
        int intv= attrs.optInt("intervalsecs", 0);
        tstPosIntArg("interval-secs", intv) ;
         _intervalMillis= 1000L * intv;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#wakeup()
     */
    protected void wakeup() {
        dispatch( new TimerEvent(this, true));        
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Poller#schedule()
     */
    protected void schedule() {        
        Date w= getWhen();       
        if (w == null) {
            scheduleRepeater( getDelayMillis(), getIntervalMillis());                        
        } else {
            scheduleRepeaterWhen( w, getIntervalMillis());            
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
        CmdLineQuestion q2= new CmdLineQuestion("delay", getResourceStr(rcb, "cmd.delay.start"), "","2") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("delaysecs", asInt(answer,2));
                return "";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("pintv", getResourceStr(rcb, "cmd.repeat.intv"), "","60") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("intervalsecs", asInt(answer,60));
                return "delay";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1,q2){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
    
}
