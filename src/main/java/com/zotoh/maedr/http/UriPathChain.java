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
 

package com.zotoh.maedr.http;

import static com.zotoh.core.util.LangUte.LT;

import java.util.Collections;
import java.util.List;

/**
 * Breaks up a http request uri into path fragments.
 * 
 * @author kenl
 */
public class UriPathChain {

	private final List<UriPathElement> _chain= LT();
	
	/**
	 * 
	 */
	public UriPathChain() {}
	
	/**
	 * @param p
	 */
	public void add(UriPathElement p) {
		if (p != null) {
			_chain.add(p);
		}
	}
	
	/**
	 * @return
	 */
	public List<UriPathElement> getElements() {
		return Collections.unmodifiableList(_chain);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder b= new StringBuilder(512);
		
		for (UriPathElement em : _chain) {
			//addAndDelim(b, "", em.toString());
			b.append(em);
		}

		return b.toString();   //"/" + b;
	}
	
	
	
	
	
}
