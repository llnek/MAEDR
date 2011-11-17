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

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.io.IOException;
import java.rmi.server.UID;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.Logger;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Pipeline;
import com.zotoh.maedr.core.Vars;

/**
 * A Device is a software component which produces events.  Each device must have a unique name (id).
 * 
 * The set of basic properties:
 * 
 * <b>id</b>
 * The name of this device, e.g. dev-1
 * <b>processor</b>
 * The processor class to handle events from this device.  If not defined, then runtime will ask the application delegate for
 * a processor.
 * 
 * @author kenl
 */
public abstract class Device implements Vars {
    
    private Logger ilog() {  return _log=getLogger(Device.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private final Map<Object, WaitEvent> _backlog = MP();
    private boolean _enabled= true,
    _status=false;
    private String _proc, _id;
    private final DeviceManager<?,?> _devMgr;
    
    /**
     * @return
     */
    public DeviceManager<?,?> getDeviceManager() {        return _devMgr;    }
    
    /**
     * @param deviceProperties
     * @throws Exception
     */
    public final void configure(JSONObject deviceProperties) throws Exception {
        tstObjArg("device-properties", deviceProperties) ;
        inizCommon(deviceProperties) ;
        inizWithProperties(deviceProperties) ;
    }
    
    /*
     * Returns true if this device supports configuration via the console - text based menu.
     * */
    public boolean supportsConfigMenu() { return false; }

    /**
     * @param rcb The bundle from which messages are read.
     * @param out This is where the captured values are placed.
     * @return false means ignore this operation, such as the user decided to cancel during input.
     * @throws IOException
     */
    public final boolean showConfigMenu(ResourceBundle rcb, Properties out) throws Exception {
    	Properties props=new Properties();
    	boolean ok=false;
    	CmdLineSequence s= supportsConfigMenu() ? getCmdSeq(rcb, props) : null;
    	if (s != null) {
    		s.start(props);
    		if ( ! s.isCanceled()) {
    			out.putAll(props); 
    			ok=true;
    		}
    	}
    	return ok;
    }

    
	/**
	 * @param rcb
	 * @param props
	 * @return
	 * @throws Exception 
	 */
	protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) throws Exception {
		String proc_str= "cmd.dev.proc";
        CmdLineQuestion p= new CmdLineQuestion("proc", getResourceStr(rcb, proc_str)) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("processor", answer);
                return "";
            }};
        final CmdLineQuestion q0= new CmdLineMandatory("dev", getResourceStr(rcb, "cmd.dev.id")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("_id", answer);
                return "proc";
            }};
            
        return new CmdLineSequence(q0,p){
            protected String onStart() {                return q0.getId();            }          
        };		
	}
	
    
    /**
     * @return if false, this device will not be *started*.
     */
    public boolean isEnabled() {        return _enabled;    }
    
    
    /**
     * Mark this device as *non start-able*.  If the device is currently active, it will be stopped. 
     */
    public void disable() {
        if ( isActive()) { stop(); }
        _enabled=false;
    }
    
    /**
     * Mark this device as *start-able*.
     */
    public void enable() {        _enabled=true;    }
    
    
    /**
     * @return true if this device is currently running - has been started.
     */
    public boolean isActive() {        return _status;    }
    
    
    /**
     * @return the identity of this device, should be unique within the set of devices managed by the device-manager.
     */
    public String getId() {        return _id;    }
    
    
    /**
     * Pushes this event downstream to the application space.
     * 
     * @param ev
     */
    public final void dispatch(Event ev) {
        _devMgr.getEngine().getJobCreator().create(ev);
    }
    
    /**
     * Activate this device.
     * 
     * @throws Exception
     */
    public final void start() throws Exception {        
        if (isEnabled()) {  
	        tlog().info( "Device: starting type= {}" , this.getClass().getName() ) ;
	        onStart();
	        _status=true;
        }
    }
    
    /**
     * Deactivate this device. 
     */
    public final void stop() {    
        if (isActive())
        try { 
            tlog().debug("Device: about to stop {}, id= {}", getClass().getName(), getId() );
            onStop(); 
        } 
        finally {
            _status=false;
        }
    }

    /**
     * If this event is currently on hold, release it as processing can be resume on this event, most likely
     * due to the application indicating that a result is ready.
     * 
     * @param w
     */
    public void releaseEvent(WaitEvent w) {
        if (w != null) { _backlog.remove(w.getId()) ; }        
    }
    
    /**
     * Hold on to this event for now (meaning queue it but ignore it for now), the downstream application will
     * process it and until a result is ready, no processing is needed for this event.
     * 
     * @param w
     */
    public void holdEvent(WaitEvent w) {
        if (w != null) { _backlog.put(w.getId(), w) ; }
    }
    
    /**
     * Internal use only.
     * 
     * @param job
     * @return
     */
    public Pipeline getPipeline(Job job) {
        return !isEmpty(_proc) ? 
                job.getEngine().getScheduler().newPipeline(_proc, job) : null;
    }
    
    /**
     * Initialize this device with a set of properties.
     * 
     * @param deviceProperties
     * @throws Exception
     */
    protected abstract void inizWithProperties(JSONObject deviceProperties) throws Exception;
    
    /**
     * @param mgr
     */
    protected Device(DeviceManager<?,?> mgr) {
        tstObjArg("device-mgr", mgr);
        _devMgr=mgr;
        _id= new UID().toString();
    }
        
    /**
     * Do something to start this device.
     * 
     * @throws Exception
     */
    protected abstract void onStart() throws Exception;
    
    /**
     * Do something to stop this device. 
     */
    protected abstract void onStop();
    
    /**
     * @param deviceProperties
     */
    protected final void inizCommon(JSONObject deviceProperties) {
        String str= trim( deviceProperties.optString( DEVID ));
        tstEStrArg("device-id", str);
        this._id= str;
        boolean b= deviceProperties.optBoolean( DEV_STATUS);        
        if (deviceProperties.has(DEV_STATUS) && b==false) {
        	// device explicitly turned off
            disable();
        }        
        _proc= trim( deviceProperties.optString( DEV_PROC ));
    }
    
    
}

