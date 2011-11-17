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
  

package demo.stockquote.java;

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.Event;

public class StockQuoteEvent extends Event {

    private static final long serialVersionUID = 2519625123316127295L;
    
    private float _change, _price;
    private String _ticker;
    
    public StockQuoteEvent(Device dev) {
        super(dev);
    }

    public void setTicker(String s) {
        _ticker=s;
    }
    
    public String getTicker() {
        return _ticker;
    }
    
    
    public void setPrice(float f) {
        _price=f;
    }
    
    public float getPrice() {
        return _price;
    }
    
    public void setChange(float f) {
        _change=f;
    }
    
    public float getChange() {
        return _change;
    }
    
    
    
    
    
}
