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


package com.zotoh.maedr.device.apache;

import org.apache.http.HttpResponse;
import org.apache.http.nio.protocol.NHttpResponseTrigger;

import com.zotoh.maedr.device.AsyncTrigger;
import com.zotoh.maedr.device.EventResult;
import com.zotoh.maedr.device.HttpEventResult;
import com.zotoh.netio.HTTPStatus;

/**
 * Asynchronous trigger - works with Apache http nio.
 * 
 * @author kenl
 */
public class NIOHttpTrigger extends AsyncTrigger {
    
    private final NHttpResponseTrigger _trigger;
    private final HttpCB _cb;
    private final HttpResponse _rsp;
    
    /**
     * @param t
     * @param cb
     * @param rsp
     */
    public NIOHttpTrigger(NHttpResponseTrigger t, HttpCB cb, HttpResponse rsp) {
    	super(cb==null ? null : cb.getDevice());
        _trigger=t;
        _cb= cb;
        _rsp= rsp;
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithResult(com.zotoh.maedr.device.EventResult)
     */
    @Override
    public void resumeWithResult(EventResult res)    {
        _cb.replyService( (HttpEventResult) res, _rsp) ;
        _trigger.submitResponse(_rsp);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.AsyncWaitTrigger#resumeWithError()
     */
    @Override
    public void resumeWithError()    {
    	_rsp.setReasonPhrase(HTTPStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    	_rsp.setStatusCode(HTTPStatus.INTERNAL_SERVER_ERROR.getCode());
        _trigger.submitResponse(_rsp);
//        handleException(HttpException httpexception);
//        handleException(IOException ioexception);
    }
    
    
}
