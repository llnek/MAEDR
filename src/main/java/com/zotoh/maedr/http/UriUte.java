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

import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.StrUte.isEmpty;

/**
 * Helper functions to parse a Uri path component.
 * 
 * @author kenl
 */
public enum UriUte {
;

	/**
	 * @param uri
	 * @return
	 */
	public static UriPathChain toPathChain(String uri) {
		UriPathChain chain= new UriPathChain();
		String p;
		int pos;
		while ((pos=uri.indexOf('/')) >= 0 ) {
			p= uri.substring(0,pos);
			if (!isEmpty(p)) { 
				chain.add( toInnerPath(p));
			}
			uri=uri.substring(pos+1);
		}
		if (uri.length() > 0) {
			chain.add( toOnePath(uri));			
		}
		
		return chain;
	}
	
	private static UriPathElement toOnePath(String path) {
		UriPathElement pe;
		
		if (path.indexOf('?') > 0) {
			pe= toQueryPath(path);
		}
		else {
			pe= toInnerPath(path);
		}
		
		return pe;
	}
	
	
	private static UriPathElement toQueryPath(String path) {
		UriPathElement pe;
		int pos=path.indexOf('?');
		
		pe= new UriPathElement(path.substring(0,pos));
		path=path.substring(pos+1);
		
		String[] ps= path.split("&");
		String pm, p,l,r;
		String[] cs;
		for (int i=0; i < ps.length; ++i) {
			p=ps[i];
			cs=p.split(";");
			p=cs[0];
			pm="";
			pos= p.indexOf('=');
			if (pos > 0) {
				pm= p.substring(0,pos);
				r= p.substring(pos+1);
				pe.addQueryParam(pm, r);
			}
			for (int j=1; j < cs.length; ++j) {
				p=cs[j];
				pos= p.indexOf('=');
				if (pos > 0) {
					l= p.substring(0,pos);
					r= p.substring(pos+1);
					pe.addMatrixParam(l, r);
				}
			}
		}
		
		
		return pe;
	}
	
	
	private static UriPathElement toInnerPath(String path) {
		String[] ps=path.split(";");
		UriPathElement pe=null;		
		int pos;
		String p,l,r;
		
		if (isNilArray(ps)) { return pe; }		
		pe=new UriPathElement(ps[0]);
		
		for (int i=1; i < ps.length; ++i) {
			p=ps[i];
			pos= p.indexOf('=');
			if (pos > 0) {
				l= p.substring(0,pos);
				r= p.substring(pos+1);
				pe.addMatrixParam(l, r);
			}
		}
		
		return pe;
	}
	
	@SuppressWarnings("unused")
	private static void main(String[] args) {
		try {
			
			System.out.println( toPathChain("/a;m=8/b;x=1;y=2;z=/?k=0;js=46456"));
			
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
