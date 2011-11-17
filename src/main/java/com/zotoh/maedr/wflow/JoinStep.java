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
 
package com.zotoh.maedr.wflow;


/**
 * @author kenl
 *
 */
public abstract class JoinStep extends FlowStep {

	protected FlowStep _body;
    protected int _cntr;
    private int _branches;

	/**
	 * @param s
	 * @param a
	 */
	protected JoinStep(FlowStep s, Join a) {
		super(s,a);
	}

	/**
	 * @param body
	 * @return
	 */
	public JoinStep withBody(FlowStep body) {
		_body=body;
        return this;
	}

    /**
     * @param n
     * @return
     */
    public JoinStep withBranches(int n) {
        _branches=n;
        return this;
    }

    /**
     * @return
     */
    public int size() { return _branches; }

	/* (non-Javadoc)
	 * @see com.zotoh.maedr.wflow.FlowStep#postRealize()
	 */
	protected void postRealize() {
		_cntr=0;
	}

}
