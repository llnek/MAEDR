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

package com.zotoh.maedr.test.flow;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.Reifier;
import com.zotoh.maedr.wflow.And;
import com.zotoh.maedr.wflow.BoolExpr;
import com.zotoh.maedr.wflow.Delay;
import com.zotoh.maedr.wflow.FlowRunner;
import com.zotoh.maedr.wflow.For;
import com.zotoh.maedr.wflow.ForLoopCountExpr;
import com.zotoh.maedr.wflow.If;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.Nihil;
import com.zotoh.maedr.wflow.Or;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Split;
import com.zotoh.maedr.wflow.Switch;
import com.zotoh.maedr.wflow.SwitchChoiceExpr;
import com.zotoh.maedr.wflow.While;
import com.zotoh.maedr.wflow.Work;

/**/
public class JUTFlows extends BaseJUT {
    	
    /**/
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUTFlows.class);
    }

    public static class Test1 extends MiniWFlow { 
    		public Test1(Job j) {super(j); }
	    protected Activity onStart() {
	    		return new Nihil() {};
	    }
    }

    protected void onOpen() throws Exception {
		_eng.bindScheduler( new FlowRunner(_eng));    	
    }
    
    @Test
    public void testSwitchInt() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final JUTFlows me=this;
		final StringBuilder bf= new StringBuilder();
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		PTask t1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		PTask t2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		PTask t3= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("yoyoma");
			}
		});

		Switch sw= new Switch()
			.withChoice(1, t1)
			.withChoice(2, t2)
			.withDef(t3);
		sw.withExpr(new SwitchChoiceExpr() {
			public Object eval(Job job) {
				return  2;
			}
		});
			
		 _eng.getScheduler().run( sw.chain(e).reify(Reifier.reifyZero(fw) ) );
		 block();
		 
		 assertTrue( "world".equals(bf.toString()));
    }
    
    @Test
    public void testSwitchString() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final JUTFlows me=this;
		final StringBuilder bf= new StringBuilder();
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		PTask t1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		PTask t2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		PTask t3= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("yoyoma");
			}
		});

		Switch sw= new Switch()
			.withChoice("1", t1)
			.withChoice("2", t2)
			.withDef(t3);
		sw.withExpr(new SwitchChoiceExpr() {
			public Object eval(Job job) {
				return "1";
			}
		});
			
		 _eng.getScheduler().run( sw.chain(e).reify(Reifier.reifyZero(fw) ) );
		 block();
		 
		 assertTrue( "hello".equals(bf.toString()));
    }
    
    @Test
    public void testSwitchDef() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final JUTFlows me=this;
		final StringBuilder bf= new StringBuilder();
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		PTask t1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		PTask t2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		PTask t3= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("yoyoma");
			}
		});

		Switch sw= new Switch()
			.withChoice("1", t1)
			.withChoice("2", t2)
			.withDef(t3);
		sw.withExpr(new SwitchChoiceExpr() {
			public Object eval(Job job) {
				return "";
			}
		});
			
		 _eng.getScheduler().run( sw.chain(e).reify(Reifier.reifyZero(fw) ) );
		 block();
		 
		 assertTrue( "yoyoma".equals(bf.toString()));
    }
    
    @Test
    public void testDelay() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final JUTFlows me=this;
		final long[] out= new long[1];
		
		out[0]=0L;
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				out[0]= System.currentTimeMillis();
				me.wake();
			}
		});
		
		long now= System.currentTimeMillis();
		Delay d= new Delay(3000);
		
		 _eng.getScheduler().run( d.chain(e).reify(Reifier.reifyZero(fw) ) );
		 block();
		 
		 assertTrue( ( out[0] - now) >= 3000);
    }
    
    @Test
    public void testSplit() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuilder bf= new StringBuilder();
		final JUTFlows me=this;
		
		Delay d= new Delay(3000);
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		final PTask s1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		final PTask s2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		Split s= new Split().addSplit(s1).addSplit(s2);
		String str;
		_eng.getScheduler().run( s.chain(d).chain(e).reify(Reifier.reifyZero(fw) ) );
		block();
		str=bf.toString();
		assertTrue(str.indexOf("hello")>=0 && str.indexOf("world")>=0);
    }

    @Test
    public void testSplitAndJoin() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuilder bf= new StringBuilder();
		final JUTFlows me=this;
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		final PTask s1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		final PTask s2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		
		Split s= new Split().addSplit(s1).addSplit(s2).withJoin(new And());
		String str;
		_eng.getScheduler().run( s.chain(e).reify(Reifier.reifyZero(fw) ) );
		block();
		str=bf.toString();
		assertTrue(str.indexOf("hello")>=0 && str.indexOf("world")>=0);
    }

    @Test
    public void testSplitOrJoin() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuilder bf= new StringBuilder();
		final JUTFlows me=this;
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		final PTask s1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.setLength(0);
				bf.append("hello");
			}
		});
		final PTask s2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.setLength(0);
				bf.append("world");
			}
		});
		
		Split s= new Split().addSplit(s1).addSplit(s2).withJoin(new Or());
		String str;
		_eng.getScheduler().run( s.chain(e).reify(Reifier.reifyZero(fw) ) );
		block();
		str=bf.toString();
		
		assertTrue(  (   str.endsWith("hello") || str.endsWith("world")  ) );
    }

    @Test
    public void testSplitJoinWithBody() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuilder bf= new StringBuilder();
		final JUTFlows me=this;
		
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		final PTask s1= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("hello");
			}
		});
		final PTask s2= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("world");
			}
		});
		final PTask bb= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				bf.append("yoyoma");
			}
		});
		
		Split s= new Split().addSplit(s1).addSplit(s2).withJoin(new And().withBody(bb));
		String str;
		_eng.getScheduler().run( s.chain(e).reify(Reifier.reifyZero(fw) ) );
		block();
		str=bf.toString();
		
		assertTrue(  (str.indexOf("hello")>=0 && str.indexOf("world")>=0 ) && str.endsWith("yoyoma") );
    }
    
    @Test
    public void testWHILE() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuffer bf= new StringBuffer();
		final int[] ctr=new int[1];
		final JUTFlows me=this;
		
		ctr[0]=0;
		PTask b= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				ctr[0]=ctr[0]+1;
				bf.append("a");
			}
		});
		PTask e= new PTask(new Work(){
			public void eval(Job job, Object closure) {
				me.wake();
			}
		});
		
		While w= new While().withBody(b);
		w.withExpr(new BoolExpr(){
			public boolean eval(Job job) {
				return ctr[0] < 5;
			}			
		});
		
		 _eng.getScheduler().run( w.chain(e).reify(Reifier.reifyZero(fw) ) );
		 block();
		 assertTrue("aaaaa".equals(bf.toString())) ;
    }
    
    @Test
    public void testIF() throws Exception {
    		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
    		final StringBuffer bf= new StringBuffer();
    		final JUTFlows me=this;
    		
    		PTask t=	 new PTask(new Work() {
					protected void eval(Job job, Object closure) {
						bf.append("helloworld");
					me.wake();
				}});
    		PTask e=	 new PTask(new Work() {
					protected void eval(Job job, Object closure) {
						bf.append("heeloo");
					me.wake();
				}});
    		If a= new If(	new BoolExpr() {
					public boolean eval(Job job) {
						return true;
					}},
					t );							
    		 _eng.getScheduler().run( a.reify(Reifier.reifyZero(fw) ) );
    		 block();
    		 assertTrue("helloworld".equals(bf.toString())) ;
    		 
    		 bf.setLength(0);
    		 a= new If(	new BoolExpr() {
 					public boolean eval(Job job) {
 						return false;
 					}},
 					t,
 					e );							
		 _eng.getScheduler().run( a.reify(Reifier.reifyZero(fw) ) );
		 block();
		 assertTrue("heeloo".equals(bf.toString())) ;
    }
    
    @Test
    public void testFOR() throws Exception {
		MiniWFlow fw= new Test1(new Job(1L, _eng, (Event)null));
		final StringBuffer bf= new StringBuffer();
		final JUTFlows me=this;
		
    		PTask t= new PTask(new Work() {
    			public void eval(Job job, Object closure) {
    				bf.append('a');
    			}
    		});
    		PTask e= new PTask(new Work() {
    			public void eval(Job job, Object closure) {
    				me.wake();
    			}
    		});
    		For f= new For(t);
    		f.withLoopCount( new ForLoopCountExpr() {
    			public int eval(Job job) {
    				return 5;
    			}
    		});
    		
   		 _eng.getScheduler().run( f.chain(e).reify(Reifier.reifyZero(fw) ) );
   		 block();
   		 assertTrue("aaaaa".equals(bf.toString())) ;
    }
    
    @Test
    public void testDummy() throws Exception {}
  
}
