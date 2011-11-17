
package com.zotoh.maedr.service;

/**
 * @author kenl
 *
 */
public class WebSocketError extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5661635729762474098L;

    /**
     * @param msg
     */
    public WebSocketError(String msg) {
        super(msg);
    }
    
    /**
     * 
     */
    public WebSocketError() {
    }


}
