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
public enum Reifier {
;

	/**
	 * @return a Nihil Step which does nothing but indicates end of flow.
	 */
	public static NihilStep reifyZero(MiniWFlow f) {
		return new NihilStep(f);
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static AsyncWaitStep reifyAsyncWait(FlowStep cur, AsyncWait a) {
		return (AsyncWaitStep) post_reify( new AsyncWaitStep(cur,a));
	}
	
	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static DelayStep reifyDelay(FlowStep cur, Delay a) {
		return (DelayStep) post_reify( new DelayStep(cur,a));
	}
	
	/**
	 * 
	 * @param cur
	 * @param a
	 * @return
	 */
	public static PTaskStep reifyPTask(FlowStep cur, PTask a) {
		return (PTaskStep) post_reify( new PTaskStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static SwitchStep reifySwitch(FlowStep cur, Switch a) {
		return (SwitchStep) post_reify( new SwitchStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static IfStep reifyIf(FlowStep cur, If a) {
		return (IfStep) post_reify( new IfStep(cur,a));
	}
	
	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static BlockStep reifyBlock(FlowStep cur, Block a) {
		return (BlockStep) post_reify( new BlockStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static SplitStep reifySplit(FlowStep cur, Split a) {
		return (SplitStep) post_reify( new SplitStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static OrStep reifyOrJoin(FlowStep cur, Or a) {
		return (OrStep) post_reify( new OrStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static NullJoinStep reifyNullJoin(FlowStep cur, NullJoin a) {
		return (NullJoinStep) post_reify( new NullJoinStep(cur,a));
	}
	
	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static AndStep reifyAndJoin(FlowStep cur, And a) {
		return (AndStep) post_reify( new AndStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static WhileStep reifyWhile(FlowStep cur, While a) {
		return (WhileStep) post_reify(new WhileStep(cur,a));
	}

	/**
	 * @param cur
	 * @param a
	 * @return
	 */
	public static ForStep reifyFor(FlowStep cur, For a) {
		return (ForStep) post_reify( new ForStep(cur,a));
	}
 
	
	private static FlowStep post_reify(FlowStep s) {
		s.realize();
		return s;
	}
	
}
