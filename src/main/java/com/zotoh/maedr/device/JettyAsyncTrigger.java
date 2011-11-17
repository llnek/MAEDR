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

import static com.zotoh.core.io.StreamUte.streamToStream;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.zotoh.core.io.StreamData;
import com.zotoh.netio.HTTPStatus;

/**
 * A trigger which works with Jetty's continuation.
 * 
 * @author kenl
 */
public class JettyAsyncTrigger extends AsyncTrigger {

    private final HttpServletResponse  _rsp;
    private final HttpServletRequest  _req;
    
    /**
     * @param dev
     * @param req
     * @param rsp
     */
    public JettyAsyncTrigger(Device dev,
            HttpServletRequest req,
            HttpServletResponse rsp) {
        super(dev);
        _rsp= rsp;
        _req=req;
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithResult(com.zotoh.maedr.device.EventResult)
     */
    @Override
    public void resumeWithResult(EventResult result) {
        Continuation c = getCont();
        
        HttpEventResult res= (HttpEventResult) result;
        Map<String,String> hdrs= res.getHeaders();
        StreamData data  = res.getData();
        try  {            
            for (String n : hdrs.keySet())            {
                if ( "content-length".equalsIgnoreCase(n)) {
                } else {
                _rsp.setHeader(n, hdrs.get(n)) ;
                }
            }            
            if (data==null || !data.hasContent()) {
                _rsp.setContentLength(0);                
            }
            if (res.hasError()) {
                _rsp.sendError(res.getStatusCode(), res.getErrorMsg()) ;
            }
            else {
                _rsp.setStatus(res.getStatusCode()) ;
            }
            if (data != null && data.hasContent()) {
                _rsp.setContentLength((int) data.getSize()) ;
                streamToStream(data.getStream(), _rsp.getOutputStream(), data.getSize()) ;
            }  
        }
        catch (Exception e) {        
            tlog().error("",e);
        }
        finally {
            c.complete();
        }
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithError()
     */
    @Override
    public void resumeWithError() {
        Continuation c = getCont();
        HTTPStatus s= HTTPStatus.INTERNAL_SERVER_ERROR;
        try {
            _rsp.sendError(s.getCode(), s.getReasonPhrase()) ;
        }
        catch (Exception e) {
            tlog().error("",e);
        }
        finally {
            c.complete();
        }
    }

    /**/
    private Continuation getCont() {
        return ContinuationSupport.getContinuation(_req);        
    }
}
