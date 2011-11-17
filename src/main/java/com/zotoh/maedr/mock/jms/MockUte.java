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
  

package com.zotoh.maedr.mock.jms;

import java.util.Random;


/**
 * @author kenl
 *
 */
public final class MockUte {

    /**
     * @return
     */
    public static String makeNewTextMsg_plus() {
        Random r= new Random();
        int a=r.nextInt(100);
        int b=r.nextInt(100);
        long c= 0L + a + b;
        return "Calc.  " + a + " + " + b + " = " + c ;
    }
    
    /**
     * @return
     */
    public static String makeNewTextMsg_x() {
        Random r= new Random();
        int a=r.nextInt(100);
        int b=r.nextInt(100);
        long c= 1L * a * b;
        return "Calc.  " + a + " * " + b + " = " + c ;
    }
    
}
