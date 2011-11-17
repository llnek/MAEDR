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


package com.zotoh.maedr.device.netty;

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.MetaUte.loadClass;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.jboss.netty.channel.SimpleChannelHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.SMap;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.core.Pipeline;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.RESTEvent;

/**
 * A Http IO device but specific to handling RESTful events.
 * 
 * The set of properties:
 * 
 * <b>contextpath</b>
 * The application context path, default is /.
 * <b>resources</b> 
 * A map of resources as path components.  Each resource is a map of name value pairs.
 * -----> <b>rpath</b> - resource path (regular expression)
 * -----> <b>processor</b> - the class name of the processor responsible for this resource.
 * 
 * @see com.zotoh.maedr.device.NettyIOTrait
 * 
 * @author kenl
 * 
 */
/**
 * @author kenl
 *
 */
public class RestIO extends NettpIO {

	private final LinkedHashMap<String,String> _resmap= new LinkedHashMap<String,String>();
	private final SMap< Constructor<?> > _pmap= new SMap< Constructor<?>>();
	private String _context;
	
    /**
     * @param m
     */
    public RestIO(DeviceManager<?,?> m) {
        super(m);
    }

    /**
     * @return
     */
    public String getContext() { return _context; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.NettyIOTrait#onStop()
     */
    @Override
	protected void onStop() {
		try { super.onStop(); } finally {  _pmap.clear(); }		
	}

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.BaseHttpIO#inizWithProperties(org.json.JSONObject)
     */
    protected void inizWithProperties(JSONObject deviceProperties) 
                throws Exception {
		super.inizWithProperties(deviceProperties);
    		
		String p, h, x= trim( deviceProperties.optString("contextpath"));
        JSONArray a=deviceProperties.optJSONArray("resources");
		int len= a!=null ? a.length() : 0;
		JSONObject obj;
		//tstEStrArg("context-path", x);
		_context=x;
		for (int i=0; i < len; ++i ) {
			obj=a.optJSONObject(i);
			if (obj==null) { continue; }
			h=trim(obj.optString("processor"));
			p=trim(obj.optString("path"));
//			tstEStrArg("resource-processor", h);
			tstEStrArg("resource-path", p);
			_resmap.put(p,h);
		}
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.NettpIO#getHandler()
     */
    protected SimpleChannelHandler getHandler() {
		return new NettpReqHdlr(this);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.NettpIO#createEvent()
     */
    protected HttpEvent createEvent() {
        return new RESTEvent(this);
    }
	
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Device#getPipeline(com.zotoh.maedr.core.Job)
     */
    public Pipeline getPipeline(Job job) {
		RESTEvent v=(RESTEvent) job.getEvent();
		String p, u=v.getUri();
		Pipeline rc=null;
		
        if (!isEmpty(_context)) {
            if ( !u.startsWith(_context)) {
                return null;
            }
            u= u.substring(_context.length());
        }
		
		// exact match ?
		if ( _resmap.containsKey(u)) {
			return newProc( _resmap.get(u), job);
		}
		
		// search for the 1st matching pattern
		for (Map.Entry<String,String> en : _resmap.entrySet()) {
			p=en.getKey();
			if ( u.matches(p)) {
    			rc= newProc( en.getValue(), job);
    			break;
			}
		}
		
		return rc==null ? super.getPipeline(job) : rc;
    }

    private Pipeline newProc(String c, Job job) {
    	
		Constructor<?> z;
		Pipeline p= null;
		
		if (!isEmpty(c))
		try {
			z= maybeFindClass(c);
			p= (Pipeline) z.newInstance(job);
		}
		catch (Exception e) {
			tlog().warn("",e);
		}
		return p;
    }
    
    /**/
    private Constructor<?> maybeFindClass(String c) throws Exception {
		
    	Constructor<?> z= _pmap.get(c);
		if (z==null) {
			z= loadClass(c).getConstructor(Job.class);
			_pmap.put(c,z);
		}
		return z;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.HttpIOTrait#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    @SuppressWarnings("unchecked")
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {
        
    	props.put("resources", new HashMap<String,String>());
        
    	CmdLineQuestion q3= new CmdLineMandatory("resproc", getResourceStr(rcb, "cmd.rest.resproc")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                Map<String,String> m = (Map<String,String>) props.get("resources");
                String uri= (String) props.remove("resuri");
                m.put(uri, answer);
                return "resptr";
            }};
        CmdLineQuestion q2= new CmdLineMandatory("resptr", getResourceStr(rcb,"cmd.rest.resptr")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                if (isEmpty(answer)) { return ""; }
                props.put("resuri", answer);
                return "resproc";
            }};
        final CmdLineQuestion q1= new CmdLineQuestion("ctx", getResourceStr(rcb, "cmd.http.ctx")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("contextpath", answer);
                return "resptr";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props), q1,q2,q3){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }
}
