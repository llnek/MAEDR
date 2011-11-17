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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.zotoh.core.util.ProcessUte;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.Activity;
import com.zotoh.maedr.wflow.Block;
import com.zotoh.maedr.wflow.MiniWFlow;
import com.zotoh.maedr.wflow.PTask;
import com.zotoh.maedr.wflow.Work;

/**/
public class JUTBlock extends BaseJUT {
    
	protected static Object OUT;
	
    /**/
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(JUTBlock.class);
    }

    static Work w1= new Work() {
    		public void eval(Job job, Object c) {
    			job.setData("lhs", "hello");
    		}
    };
    
    static Work w2= new Work() {
		public void eval(Job job, Object c) {
			job.setData("rhs", "world");			
		}
    };

    static Work end= new Work() {
		public void eval(Job job, Object c) {
			OUT= (String) job.getData("lhs") + (String) job.getData("rhs") ;
			JUTBlock b= (JUTBlock)getCurStep().getFlow().getEngineProperties().get("_");
			b._eng.shutdown();		
		}
    };
    
    public static class Test1 extends MiniWFlow { 
    		public Test1(Job j) {super(j); }
	    protected Activity onStart() {
	        return new Block().chain(new PTask(w1)).chain( new PTask(w2)).chain(new PTask(end));
	    }
    }
    
    //@Test
    public void test1() throws Exception {
    		Properties props=create_props("com.zotoh.maedr.test.flow.JUTBlock$Test1"); 
    		OUT=null;
    		props.put("_", this);
    		_eng.start(props );
    		ProcessUte.safeThreadWait(3000);
		assertEquals(OUT, "helloworld");
    }
    
    @Test
    public void testDummy() throws Exception {}
  
}
