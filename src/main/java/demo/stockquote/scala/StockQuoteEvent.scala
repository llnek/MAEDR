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

import com.zotoh.maedr.device.{Event,Device}

@SerialVersionUID(2519625123316127295L)
class StockQuoteEvent(dev:Device) extends Event(dev) {
    
    private var _change=0.0
    private var _price=0.0
    private var _ticker=""

    def change=_change
    def price=_price
    def ticker=_ticker

    def change_=(c:Float) : Unit =  { _change=c }
    def price_=(p:Float) : Unit =  { _price=p }
    def ticker_=(s:String) : Unit =  { _ticker=s }
    
}
