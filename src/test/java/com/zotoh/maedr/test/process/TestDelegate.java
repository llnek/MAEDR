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
 

package com.zotoh.maedr.test.process;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import com.zotoh.core.io.StreamData;
import com.zotoh.maedr.core.AppDelegate;
import com.zotoh.maedr.core.AppEngine;
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.device.Event;
import com.zotoh.maedr.device.FileEvent;
import com.zotoh.maedr.device.HttpEvent;
import com.zotoh.maedr.device.POP3Event;
import com.zotoh.maedr.device.TimerEvent;
import com.zotoh.maedr.device.netty.WebSockEvent;
import com.zotoh.maedr.process.FlowInfo;
import com.zotoh.maedr.process.Processor;
import com.zotoh.maedr.process.Stateless;
import com.zotoh.maedr.process.WorkUnit;
import com.zotoh.netio.BasicHttpMsgIO;
import com.zotoh.netio.HttpUte;

/**
 * @author kenl
 *
 */
public class TestDelegate extends AppDelegate<Processor,Processor> {

    /**/
    public TestDelegate(AppEngine<Processor,Processor> eng)     {
        super(eng);
    }

    @Override
    public Processor newProcess(Job job)     {
        Event ev= job.getEvent();
        Processor p=null;
        
        if (ev instanceof WebSockEvent) {
            p= new WebSockProcessor(job);
        }
        else
        if (ev instanceof FileEvent) {
            p= new FileProcessor(job);
        }
        else
        if (ev instanceof HttpEvent) {
            p= new HttpProcessor(job);            
        }
        else
        if (ev instanceof POP3Event) {
            p= new POP3Proc(job);
        }
        else
        if (ev instanceof TimerEvent) {
            p= new TProc(job);
        }
        
        return p;
    }

    
}

class POP3Proc extends Stateless {

    class Task1 extends WorkUnit {
        public Task1(Processor proc) {
            super(proc, "t1");
        }
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            POP3Event e= (POP3Event) job.getEvent();
            byte[] bits=e.getMsg().getBytes();
            System.out.println("########################");
            System.out.println("subject> " + e.getSubject());
            System.out.println("from> " + e.getFrom());
            System.out.println("to> " + e.getTo());
            System.out.println(new String(bits,"utf-8"));
            return null;
        }        
    }
    
    protected POP3Proc(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        switch (getCurStepPos()) {
        case 10001: return new Task1(this);
        }
        return null;
    }

    @Override
    protected FlowInfo onStart(Event e) {
        return new FlowInfo(10001);
    }
    
}

class TProc extends Stateless {

    class Task1 extends WorkUnit {
        public Task1(Processor proc) {
            super(proc, "t1");
        }

        @Override
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            
            
            Timer t= new Timer(true);
            t.schedule(new TimerTask() {
                public void run() {
                }}, 3000);
            

            return new FlowInfo(10002);
        }
        
    }
    class Task2 extends WorkUnit {
        public Task2(Processor proc) {
            super(proc, "t2");
        }

        @Override
        protected FlowInfo evalOneStep(Job job, Object closureArg)
                throws Exception {
            try {
                HttpUte.simpleGET(new URI("http://localhost:7051/kill9?pwd=stopengine"),
                    new BasicHttpMsgIO() {
                        public void onOK(int code, String reason,
                                StreamData responseData) {
                        }
                        public void onError(int code, String reason) {
                        }                    
                });
            }
            catch (Throwable t) 
            {}            
            return null;
        }
        
    }
    
    public TProc(Job j) {
        super(j);
    }

    @Override
    protected WorkUnit getNextWorkUnit() {
        int step= getCurStepPos();
        switch (step) {
        case 10001: return new Task1(this);
        case 10002: return new Task2(this);
        }
        return null;
    }

    @Override
    protected FlowInfo onStart(Event e) {
        return new FlowInfo(10001);
    }
    
}
