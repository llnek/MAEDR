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


package com.zotoh.maedr.device.apache;

import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.AsyncNHttpServiceHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpRequestHandlerResolver;
import org.apache.http.nio.protocol.SimpleNHttpRequestHandler;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExpectationVerifier;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;

/**
 * @author kenl
 *
 */
public class StreamedHttpServiceHandler implements NHttpServiceHandler {

    private final AsyncNHttpServiceHandler asyncHandler;
    private HttpRequestHandlerResolver handlerResolver;

    /**
     * @param httpProcessor
     * @param responseFactory
     * @param connStrategy
     * @param allocator
     * @param params
     */
    public StreamedHttpServiceHandler(
            final HttpProcessor httpProcessor,
            final HttpResponseFactory responseFactory,
            final ConnectionReuseStrategy connStrategy,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        this.asyncHandler = new AsyncNHttpServiceHandler(
                httpProcessor,
                responseFactory,
                connStrategy,
                allocator,
                params);
        this.asyncHandler.setHandlerResolver(new RequestHandlerResolverAdaptor());
    }

    /**
     * @param httpProcessor
     * @param responseFactory
     * @param connStrategy
     * @param params
     */
    public StreamedHttpServiceHandler(
            final HttpProcessor httpProcessor,
            final HttpResponseFactory responseFactory,
            final ConnectionReuseStrategy connStrategy,
            final HttpParams params) {
        this(httpProcessor, responseFactory, connStrategy,
                new HeapByteBufferAllocator(), params);
    }

    /**
     * @param eventListener
     */
    public void setEventListener(final EventListener eventListener) {
        this.asyncHandler.setEventListener(eventListener);
    }

    /**
     * @param expectationVerifier
     */
    public void setExpectationVerifier(final HttpExpectationVerifier expectationVerifier) {
        this.asyncHandler.setExpectationVerifier(expectationVerifier);
    }

    /**
     * @param handlerResolver
     */
    public void setHandlerResolver(final HttpRequestHandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#connected(org.apache.http.nio.NHttpServerConnection)
     */
    public void connected(final NHttpServerConnection conn) {
        this.asyncHandler.connected(conn);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#closed(org.apache.http.nio.NHttpServerConnection)
     */
    public void closed(final NHttpServerConnection conn) {
        this.asyncHandler.closed(conn);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#requestReceived(org.apache.http.nio.NHttpServerConnection)
     */
    public void requestReceived(final NHttpServerConnection conn) {
        this.asyncHandler.requestReceived(conn);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#inputReady(org.apache.http.nio.NHttpServerConnection, org.apache.http.nio.ContentDecoder)
     */
    public void inputReady(final NHttpServerConnection conn, final ContentDecoder decoder) {
        this.asyncHandler.inputReady(conn, decoder);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#responseReady(org.apache.http.nio.NHttpServerConnection)
     */
    public void responseReady(final NHttpServerConnection conn) {
        this.asyncHandler.responseReady(conn);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#outputReady(org.apache.http.nio.NHttpServerConnection, org.apache.http.nio.ContentEncoder)
     */
    public void outputReady(final NHttpServerConnection conn, final ContentEncoder encoder) {
        this.asyncHandler.outputReady(conn, encoder);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#exception(org.apache.http.nio.NHttpServerConnection, org.apache.http.HttpException)
     */
    public void exception(final NHttpServerConnection conn, final HttpException httpex) {
        this.asyncHandler.exception(conn, httpex);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#exception(org.apache.http.nio.NHttpServerConnection, java.io.IOException)
     */
    public void exception(final NHttpServerConnection conn, final IOException ioex) {
        this.asyncHandler.exception(conn, ioex);
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.NHttpServiceHandler#timeout(org.apache.http.nio.NHttpServerConnection)
     */
    public void timeout(NHttpServerConnection conn) {
        this.asyncHandler.timeout(conn);
    }

    /**/
    class RequestHandlerResolverAdaptor implements NHttpRequestHandlerResolver {
        public NHttpRequestHandler lookup(final String requestURI) {
            HttpRequestHandler handler = handlerResolver.lookup(requestURI);
            if (handler != null) {
                return new RequestHandlerAdaptor(handler);
            } else {
                return null;
            }
        }
    }

    /**/
    static class RequestHandlerAdaptor extends SimpleNHttpRequestHandler {
        private final HttpRequestHandler requestHandler;
        public RequestHandlerAdaptor(final HttpRequestHandler requestHandler) {
            this.requestHandler = requestHandler;
        }
        public ConsumingNHttpEntity entityRequest(
                final HttpEntityEnclosingRequest request,
                final HttpContext context) throws HttpException, IOException {
            return new StreamingNHttpEntity(
                    request.getEntity(),
                    new HeapByteBufferAllocator());
        }
        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {
            this.requestHandler.handle(request, response, context);
        }
    }

}
