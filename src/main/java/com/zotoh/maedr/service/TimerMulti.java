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
 package com.zotoh.maedr.service;

 /**
 * @author kenl
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
 public abstract class TimerMulti<T> extends TimerOne<T> {
     
     /**
     * @param repeatsecs
     */
    protected TimerMulti(int repeatsecs) {
         intervalsecs(repeatsecs);
     }
     
     /**
     * @param secs
     * @return
     */
    public T intervalsecs(int secs) {
         safePutProp("intervalsecs", secs);
         return (T)this;
     }
     
 }
 