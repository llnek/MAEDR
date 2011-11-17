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
public abstract class Conditional extends Activity {

	private BoolExpr _expr;
	
	/**
	 * @param expr
	 */
	public Conditional(BoolExpr expr) {        _expr=expr;	}

	/**
	 * 
	 */
	public Conditional() {  }
	
    /**
     * @return
     */
    public BoolExpr getExpr() { return _expr; }

	/**
	 * @param e
	 * @return
	 */
	public Conditional withExpr(BoolExpr e) {
		_expr=e; return this;
	}
	
    
}
