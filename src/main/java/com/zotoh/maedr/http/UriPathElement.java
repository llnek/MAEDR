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

import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.core.util.StrUte.nsb;
import static com.zotoh.core.util.StrUte.trim;

import java.util.Map;

import com.zotoh.core.util.StrArr;

/**
 * A path within a HTTP request uri.
 * 
 * @author kenl
 */
public class UriPathElement {

	private final Map<String,StrArr> _matrixParams= MP() ,
	_queryParams= MP();
	
	private final String _path;
	
	
	/**
	 * @param p
	 */
	public UriPathElement(String p) {
		p=trim(p);
		if ( p.startsWith("/")) { _path =p; } else { _path= "/" + p; }
	}

	/**
	 * @param name
	 * @return
	 */
	public StrArr getMatrixParam(String name) { 
		return name==null ? null : _matrixParams.get(name); 
	}
	
	/**
	 * @return
	 */
	public String getPath() { return _path; }
	
	/**
	 * @param name
	 * @param value
	 */
	public void addMatrixParam(String name, String value) {
		tstEStrArg("param-name", name);
		StrArr a;
		if ( ! _matrixParams.containsKey(name)) {
			a=new StrArr();
			_matrixParams.put(name, a);
		} else {
			a= _matrixParams.get(name);
		}
		a.add( nsb(value));
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public void addQueryParam(String name, String value) {
		tstEStrArg("param-name", name);
		StrArr a;
		if ( ! _queryParams.containsKey(name)) {
			a=new StrArr();
			_queryParams.put(name, a);		
		} else {
			a= _queryParams.get(name);
		}
		a.add( nsb(value));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder b2, b1= new StringBuilder(512);
		String p;
		for (Map.Entry<String,StrArr> en : _queryParams.entrySet()) {
			p=en.getKey() + "=" + en.getValue().toString();
			addAndDelim(b1, "&", p);
		}
		
		b2= new StringBuilder(512);
		for (Map.Entry<String,StrArr> en : _matrixParams.entrySet()) {
			p=en.getKey() + "=" + en.getValue().toString();
			addAndDelim(b2, ";", p);
		}		
		
		return _path + ((_queryParams.size() > 0) ? "?" : "") + b1 +
						((_matrixParams.size() > 0) ? ";" : "") + b2;
	}
	
	
	
}
