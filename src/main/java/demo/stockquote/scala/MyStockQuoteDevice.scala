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
  
package demo.stockquote

import _root_.java.io.{BufferedReader,InputStreamReader}
import _root_.java.net.{URI,URLConnection,HttpURLConnection}

import com.zotoh.maedr.device.{ThreadedTimer,DeviceManager}
import com.zotoh.core.util.CoreUte
import org.json.{JSONObject,JSONArray}


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
class MyStockQuoteDevice(mgr:DeviceManager[_,_])
extends ThreadedTimer(mgr) {
//extends Device {

    private var _symbols=""
    private var _url=""
    
    override def inizWithProperties(attrs:JSONObject) : Unit = {
        super.inizWithProperties(attrs) 
        
        // most of the properties are taken care of from the superclass
        // we just need to get the "url" & the "symbols" which are new to this device
        
        _url = attrs.optString("url")
        _symbols=""
        
        // check to make sure we have some value, else throw a runtime error
        CoreUte.tstEStrArg("url", _url)
                
        val arr=attrs.optJSONArray("symbols")
        CoreUte.tstObjArg("symbols", arr)
        val len=arr.length() - 1
        var sym=""

        // check to make sure we have some value, else throw a runtime error
        
        for (i <- 0 to len) {
            sym=arr.optString(i)
            CoreUte.tstEStrArg("symb", sym)
            if (_symbols.length() > 0) { _symbols = _symbols +  "," }
            _symbols = _symbols + sym
        }
        
    }
    
    override def onOneLoop() : Unit = {
        
        // do your stuff
                
        val conn= new URI( _url.replaceAll("SYMBOL", _symbols)).toURL().openConnection()
        try {
            val rdr= new BufferedReader(new InputStreamReader(conn.getInputStream()))
            var line=rdr.readLine()
            while (line != null) {
                val infos = line.split(",")
                val ev=new StockQuoteEvent(this)
                ev.ticker= infos(0).replaceAll("\"", "")
                ev.price= infos(1).toFloat
                ev.change= infos(4).toFloat
                
                // send it downstream to application
                dispatch(ev)

                line=rdr.readLine()
            }
        } 
        finally {
            conn.asInstanceOf[HttpURLConnection].disconnect()
        }
        
        
    }

}
