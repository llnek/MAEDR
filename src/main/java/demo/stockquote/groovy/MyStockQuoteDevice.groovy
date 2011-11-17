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
  
package demo.stockquote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zotoh.core.util.CoreUte;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.ThreadedTimer;

/**
 * This example shows you how to create your own device.  Your device
 * must be a subclass of the base class Device.  However, since in this
 * case we are building a stock quote reader which periodically
 * reads quotes, we want to subclass from the built-in ThreadedTimer
 * device which already takes care of all the polling functions.
 * 
 * 
 * @author kenl
 *
 */
class MyStockQuoteDevice
//extends Device {
extends ThreadedTimer {

    private def _symbols, _url;
    
    /**
     * @param mgr
     */
    def MyStockQuoteDevice(DeviceManager<?, ?> mgr) {
        super(mgr);
    }

    void inizWithProperties(JSONObject attrs) {
        super.inizWithProperties(attrs) ;
        
        // most of the properties are taken care of from the superclass
        // we just need to get the "url" & the "symbols" which are new to this device
        
        _url = attrs.optString("url");
        _symbols="";
        
        // check to make sure we have some value, else throw a runtime error
        CoreUte.tstEStrArg("url", _url);
                
        def sym, arr=attrs.optJSONArray("symbols");
        // check to make sure we have some value, else throw a runtime error
        CoreUte.tstObjArg("symbols", arr);
        
        for (int i=0; i < arr.length(); ++i) {
            sym=arr.optString(i);
            CoreUte.tstEStrArg("stock-quote", sym);
            if (_symbols.length() > 0) { _symbols += ","; }
            _symbols += sym;
        }
        
    }
    
    void onOneLoop() {
        
        // do your stuff
        def conn;
        def line, url;        
        def rdr;
        def infos;
        def ev;
                
        url = _url.replaceAll("SYMBOL", _symbols);
        conn= new URI(url).toURL().openConnection();
        try {
            rdr= new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rdr.readLine()) != null) {
                infos = line.split(",");
                ev=new StockQuoteEvent(this);
                ev.setTicker(infos[0].replaceAll("\"", ""));                
                ev.setPrice(Float.valueOf(infos[1]));                
                ev.setChange(Float.valueOf(infos[4]));
                
                // send it downstream to application
                dispatch(ev);
            }
        } 
        finally {
            if (conn instanceof HttpURLConnection) {
                ( (HttpURLConnection) conn).disconnect();
            }
        }
        
        
    }

}
