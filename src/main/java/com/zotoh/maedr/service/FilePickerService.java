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
package com.zotoh.maedr.service;

import com.zotoh.maedr.device.Device;
import com.zotoh.maedr.device.DeviceManager;
import com.zotoh.maedr.device.FileEvent;
import com.zotoh.maedr.device.FilePicker;

/**
 * @author kenl
 *
 */
public class FilePickerService extends TimerMulti<FilePickerService> {

    private FileFoundHandler _hdlr;
    
    /**
     * @param folderPath
     * @return
     */
    public static FilePickerService create(String folderPath) {
        return new FilePickerService(folderPath);
    }

    /**
     * @param move
     * @return
     */
    public FilePickerService automove(boolean move) {
        safePutProp("automove", move);
        return this;
    }
    
    /**
     * @param dirToMoveTo
     * @return
     */
    public FilePickerService destdir(String dirToMoveTo) {
        safePutProp("destdir", dirToMoveTo);
        return this;
    }
    
    /**
     * @param mask
     * @return
     */
    public FilePickerService filemask(String mask) {
        safePutProp("fmask", mask);
        return this;
    }

    /**
     * @param h
     * @return
     */
    public FilePickerService handler(FileFoundHandler h) {
        _hdlr=h;
        return this;
    }
    
    private FilePickerService(String folderPath) {
        super(60);
        safePutProp("rootdir", folderPath);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#newDevice(com.zotoh.maedr.device.DeviceManager)
     */
    @Override
    protected Device newDevice(DeviceManager<?,?> m) throws Exception {
        return new FilePicker(m);
    }

    /* (non-Javadoc)
     * @see com.zotoh.maedr.service.ServiceIO#getCB()
     */
    @Override
    public ServiceCB<FileEvent> getCB() {
        return new ServiceCB<FileEvent>() {
            public void handleEvent(FileEvent ev) {
                _hdlr.eval(ev);
            }
            public Class<FileEvent> getEventType() {
                return FileEvent.class;
            }
        };
    }

    
    
}


