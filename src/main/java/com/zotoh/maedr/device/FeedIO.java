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

package com.zotoh.maedr.device;

import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;

/*
 * A device which reads data from a set of RSS Atom feeds.
 * 
 * The set of properties:
 * 
 * <b>urls</b>
 * The set of remote RSS Feed URLs.
 *  
 * @see com.zotoh.maedr.device.RepeatingTimer
 * 
 * @author kenl
 */
public class FeedIO extends ThreadedTimer {

    private final List<URI> _urls= LT();
    private boolean _validate;
    
    /**
     * @param mgr
     */
    public FeedIO(DeviceManager<?,?> mgr) {
        super(mgr);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#inizWithProperties(org.json.JSONObject)
     */
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception {
        super.inizWithProperties(deviceProperties);
        
        boolean check=deviceProperties.optBoolean("validate");
        String s;
        JSONArray a=deviceProperties.optJSONArray("urls");
        tstObjArg("feed-urls", a);     
        for (int i=0; i < a.length(); ++i) {
            s=trim(a.optString(i));
            if ( ! isEmpty(s)) {
            	_urls.add( new URI(s));
            }
        }
        _validate=check;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#preLoop()
     */
    @Override
    protected void preLoop() throws Exception {}

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#endLoop()
     */
    @Override
    protected void endLoop() {}

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#onOneLoop()
     */
    @Override
    protected void onOneLoop() throws Exception {
        SyndFeedInput input = new SyndFeedInput(_validate);
//        SyndFeed feed;
        for (URI u : _urls) {            
            dispatch( new FeedEvent(this, 
                    u.toASCIIString(),
                    input.build(new XmlReader(u.toURL()))));
        }
    }

    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { 	return true; }
    
    /**/
    @SuppressWarnings("unchecked")
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {
        props.put("urls", new ArrayList<String>());        
        final CmdLineQuestion q1= new CmdLineMandatory("url", getResourceStr(rcb, "cmd.feed.url")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                List<String> c= (List<String>) props.get("urls");
                if (isEmpty(answer)) { return ""; }
                c.add(answer);
                return getId();
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props), q1){
            protected String onStart() {
                return q1.getId();
            }           
        };
        
    }
    
}
