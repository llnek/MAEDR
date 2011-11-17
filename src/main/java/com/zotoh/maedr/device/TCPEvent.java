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

import static com.zotoh.core.util.StrUte.nsb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.zotoh.netio.NetUte;

/**
 * Events generated by a TCP device.
 *
 * @author kenl
 */
public class TCPEvent extends Event {

    private static final long serialVersionUID = -3310871938767923755L;
    private Socket _soc;
    private String _enc= "utf-8" ;
    private boolean _binary= true;
    
    /**
     * @param dev
     * @param soc
     */
    public TCPEvent(Device dev, Socket soc)     {
        super(dev);
        _soc=soc;
    }
    
    /**
     * @param millis
     * @throws SocketException
     */
    protected void setSocketTimeout(int millis) throws SocketException {
        _soc.setSoTimeout(millis);
    }
    
    /**
     * @param enc
     */
    protected void setEncoding(String enc) {
        _enc= nsb(enc);
    }
    
    /**
     * @param binary
     */
    protected void setBinary(boolean binary) {
        _binary= binary;
    }
        
    /**
     * @return
     * @throws IOException
     */
    public OutputStream getSockOut() throws IOException     {
        return _soc==null ? null : _soc.getOutputStream();
    }

    /**
     * @return
     * @throws IOException
     */
    public InputStream getSockIn() throws IOException     {
        return _soc==null ? null : _soc.getInputStream();
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.Event#destroy()
     */
    @Override
    public void destroy()     {
        _soc= NetUte.close(_soc);
    }

    /**
     * @return
     */
    public boolean isBinary() {        return _binary;    }
    
    /**
     * @return
     */
    public String getEncoding() {        return _enc;    }
    
    
}
